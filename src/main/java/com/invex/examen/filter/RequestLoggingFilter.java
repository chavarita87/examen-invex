package com.invex.examen.filter;

import com.invex.examen.constant.LoggingConstants;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Logs every request with its headers, status and duration. Sensitive headers are masked.
 * Each request gets a correlation id, taken from {@value LoggingConstants#CORRELATION_ID_HEADER}
 * when the caller sends a valid one, that is added to every log line and returned in the response.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);
        MDC.put(LoggingConstants.CORRELATION_ID_MDC_KEY, correlationId);
        response.setHeader(LoggingConstants.CORRELATION_ID_HEADER, correlationId);
        long start = System.nanoTime();
        try {
            log.info("Request {} {} headers={}", request.getMethod(), requestPath(request), maskedHeaders(request));
            chain.doFilter(request, response);
        } finally {
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            log.info("Response {} {} status={} durationMs={}", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsedMillis);
            MDC.remove(LoggingConstants.CORRELATION_ID_MDC_KEY);
        }
    }

    /**
     * Reuses the caller's correlation id only when it is short and safe to log;
     * otherwise generates a new one, so the header cannot be used to inject text into the logs.
     */
    private static String resolveCorrelationId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(LoggingConstants.CORRELATION_ID_HEADER))
                .filter(id -> id.length() <= LoggingConstants.CORRELATION_ID_MAX_LENGTH)
                .filter(id -> id.matches(LoggingConstants.CORRELATION_ID_PATTERN))
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private static String requestPath(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(query -> request.getRequestURI() + "?" + query)
                .orElse(request.getRequestURI());
    }

    private static Map<String, String> maskedHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(Function.identity(), name -> headerValue(request, name),
                        (first, second) -> first, TreeMap::new));
    }

    private static String headerValue(HttpServletRequest request, String name) {
        if (LoggingConstants.SENSITIVE_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
            return LoggingConstants.MASKED_VALUE;
        }
        return String.join(",", Collections.list(request.getHeaders(name)));
    }
}

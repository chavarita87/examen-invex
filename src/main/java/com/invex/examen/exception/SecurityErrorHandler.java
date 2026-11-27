package com.invex.examen.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invex.examen.constant.SecurityConstants;
import com.invex.examen.dto.ErrorResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Writes 401 and 403 responses with the same {@link ErrorResponse} body as every other error.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final String BASIC_CHALLENGE = "Basic realm=\"" + SecurityConstants.REALM + "\"";

    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        log.warn("Unauthenticated request to {} {}", request.getMethod(), request.getRequestURI());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, BASIC_CHALLENGE);
        write(response, HttpStatus.UNAUTHORIZED, SecurityConstants.UNAUTHORIZED_MESSAGE, request.getRequestURI());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        log.warn("Access denied to {} {} for user {}", request.getMethod(), request.getRequestURI(),
                request.getRemoteUser());
        write(response, HttpStatus.FORBIDDEN, SecurityConstants.FORBIDDEN_MESSAGE, request.getRequestURI());
    }

    private void write(HttpServletResponse response, HttpStatus status, String message, String path)
            throws IOException {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .fieldErrors(List.of())
                .build();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}

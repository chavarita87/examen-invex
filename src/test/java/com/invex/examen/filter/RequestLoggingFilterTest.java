package com.invex.examen.filter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.invex.examen.constant.LoggingConstants;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();
    private final Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
        request = new MockHttpServletRequest("GET", "/employees/search");
        request.setQueryString("name=jua");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    @DisplayName("logs the request line, headers, status and duration")
    void logsRequestAndResponse() throws Exception {
        request.addHeader("Accept", "application/json");

        filter.doFilter(request, response, (req, res) -> ((MockHttpServletResponse) res).setStatus(200));

        assertThat(appender.list).hasSize(2);
        assertThat(appender.list.get(0).getFormattedMessage())
                .startsWith("Request GET /employees/search?name=jua headers=")
                .contains("Accept=application/json");
        assertThat(appender.list.get(1).getFormattedMessage())
                .startsWith("Response GET /employees/search status=200 durationMs=");
    }

    @Test
    @DisplayName("masks sensitive headers regardless of case")
    void masksSensitiveHeaders() throws Exception {
        request.addHeader("Authorization", "Bearer secret-token");
        request.addHeader("X-API-KEY", "secret-key");
        request.addHeader("Cookie", "session=secret");

        filter.doFilter(request, response, (req, res) -> { });

        String requestLog = appender.list.get(0).getFormattedMessage();
        assertThat(requestLog)
                .contains("Authorization=****", "X-API-KEY=****", "Cookie=****")
                .doesNotContain("secret");
    }

    @Test
    @DisplayName("reuses a valid correlation id and exposes it in the logs and the response")
    void reusesValidCorrelationId() throws Exception {
        request.addHeader(LoggingConstants.CORRELATION_ID_HEADER, "abc-123");
        AtomicReference<String> idDuringRequest = new AtomicReference<>();

        filter.doFilter(request, response,
                (req, res) -> idDuringRequest.set(MDC.get(LoggingConstants.CORRELATION_ID_MDC_KEY)));

        assertThat(idDuringRequest.get()).isEqualTo("abc-123");
        assertThat(response.getHeader(LoggingConstants.CORRELATION_ID_HEADER)).isEqualTo("abc-123");
        assertThat(appender.list.get(0).getMDCPropertyMap())
                .containsEntry(LoggingConstants.CORRELATION_ID_MDC_KEY, "abc-123");
    }

    @Test
    @DisplayName("generates a correlation id when none is sent")
    void generatesCorrelationId() throws Exception {
        filter.doFilter(request, response, (req, res) -> { });

        String id = response.getHeader(LoggingConstants.CORRELATION_ID_HEADER);
        assertThat(UUID.fromString(id)).isNotNull();
    }

    @Test
    @DisplayName("replaces a correlation id that could inject text into the logs")
    void replacesUnsafeCorrelationId() throws Exception {
        request.addHeader(LoggingConstants.CORRELATION_ID_HEADER, "abc\nFAKE LOG LINE");

        filter.doFilter(request, response, (req, res) -> { });

        String id = response.getHeader(LoggingConstants.CORRELATION_ID_HEADER);
        assertThat(id).doesNotContain("FAKE");
        assertThat(UUID.fromString(id)).isNotNull();
    }

    @Test
    @DisplayName("replaces a correlation id that is too long")
    void replacesTooLongCorrelationId() throws Exception {
        request.addHeader(LoggingConstants.CORRELATION_ID_HEADER, "a".repeat(65));

        filter.doFilter(request, response, (req, res) -> { });

        assertThat(UUID.fromString(response.getHeader(LoggingConstants.CORRELATION_ID_HEADER))).isNotNull();
    }

    @Test
    @DisplayName("clears the correlation id from the MDC after the request, even when it fails")
    void clearsMdcAfterFailure() {
        try {
            filter.doFilter(request, response, (req, res) -> {
                throw new IllegalStateException("boom");
            });
        } catch (Exception expected) {
            assertThat(expected).hasMessage("boom");
        }

        assertThat(MDC.get(LoggingConstants.CORRELATION_ID_MDC_KEY)).isNull();
        assertThat(appender.list).hasSize(2);
    }
}

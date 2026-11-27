package com.invex.examen.constant;

import java.util.Set;

/**
 * Constants used by request logging.
 */
public final class LoggingConstants {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final int CORRELATION_ID_MAX_LENGTH = 64;
    public static final String CORRELATION_ID_PATTERN = "^[A-Za-z0-9-]+$";

    public static final String MASKED_VALUE = "****";

    /**
     * Headers whose values must never be written to the logs (compared in lower case).
     */
    public static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key");

    private LoggingConstants() {
    }
}

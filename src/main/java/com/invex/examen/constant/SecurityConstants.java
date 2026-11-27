package com.invex.examen.constant;

/**
 * Roles, public paths and authentication settings.
 */
public final class SecurityConstants {

    public static final String ROLE_READER = "READER";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String REALM = "employees";
    public static final String BASIC_AUTH_SCHEME = "basicAuth";

    public static final String EMPLOYEES_PATTERN = ApiConstants.EMPLOYEES_PATH + "/**";

    public static final String[] PUBLIC_PATHS = {
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/error"
    };

    public static final String UNAUTHORIZED_MESSAGE = "Authentication is required to access this resource";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to perform this operation";

    private SecurityConstants() {
    }
}

package com.invex.examen.config;

import com.invex.examen.constant.SecurityConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * General information shown in the OpenAPI contract and Swagger UI, including HTTP Basic security.
 */
@Configuration
public class OpenApiConfig {

    private static final String TITLE = "Employee Service API";
    private static final String DESCRIPTION = "REST API to create, read, update, delete and search employees. "
            + "Dates use the format dd-MM-yyyy. Every error returns the same ErrorResponse body. "
            + "Requires HTTP Basic authentication: role READER can read, role ADMIN can also write.";
    private static final String LICENSE = "Apache 2.0";
    private static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";
    private static final String BASIC_SCHEME = "basic";

    @Bean
    public OpenAPI employeeOpenApi(@Value("${spring.application.version:1.0.0}") String version) {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .description(DESCRIPTION)
                        .version(version)
                        .license(new License().name(LICENSE).url(LICENSE_URL)))
                .components(new Components()
                        .addSecuritySchemes(SecurityConstants.BASIC_AUTH_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(BASIC_SCHEME)))
                .addSecurityItem(new SecurityRequirement().addList(SecurityConstants.BASIC_AUTH_SCHEME));
    }
}

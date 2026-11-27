package com.invex.examen.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class SecurityPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(PropertiesConfig.class);

    @Configuration
    @EnableConfigurationProperties(SecurityProperties.class)
    static class PropertiesConfig {
    }

    @Test
    @DisplayName("loads the users when every credential is provided")
    void loadsCompleteCredentials() {
        runner.withPropertyValues(
                        "app.security.reader.username=reader", "app.security.reader.password=reader-pass",
                        "app.security.admin.username=admin", "app.security.admin.password=admin-pass")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(SecurityProperties.class).admin().username()).isEqualTo("admin");
                });
    }

    @Test
    @DisplayName("refuses to start when the credentials are missing")
    void failsWithoutCredentials() {
        runner.run(context -> assertThat(context).hasFailed());
    }

    @Test
    @DisplayName("refuses to start with a password shorter than 8 characters")
    void failsWithShortPassword() {
        runner.withPropertyValues(
                        "app.security.reader.username=reader", "app.security.reader.password=short",
                        "app.security.admin.username=admin", "app.security.admin.password=admin-pass")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    @DisplayName("never prints the password")
    void masksPasswordInToString() {
        SecurityProperties.Credentials credentials = new SecurityProperties.Credentials("admin", "secret-pass");

        assertThat(credentials.toString()).contains("admin").doesNotContain("secret-pass");
    }
}

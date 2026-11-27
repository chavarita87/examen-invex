package com.invex.examen.config;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * API users read from configuration ({@code app.security.*}). Credentials come from
 * environment variables and are validated at startup, so the service refuses to start without them.
 *
 * @param reader user allowed to read employees
 * @param admin  user allowed to read and change employees
 */
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(@Valid @NotNull Credentials reader, @Valid @NotNull Credentials admin) {

    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Username and plain-text password of one API user; the password is hashed when loaded.
     */
    public record Credentials(@NotBlank String username, @NotBlank @Size(min = MIN_PASSWORD_LENGTH) String password) {

        @Override
        public String toString() {
            return "Credentials[username=" + username + ", password=****]";
        }
    }
}

package com.invex.examen.config;

import com.invex.examen.constant.SecurityConstants;
import com.invex.examen.exception.SecurityErrorHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP Basic authentication with two roles: {@code READER} can only read employees,
 * {@code ADMIN} can also create, update and delete them. Health, info and the API docs are public;
 * any other path is denied.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityErrorHandler errorHandler)
            throws Exception {
        http
                // Stateless REST API authenticated on every request: no session or cookie to protect with CSRF.
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(auth -> auth
                        .antMatchers(SecurityConstants.PUBLIC_PATHS).permitAll()
                        .antMatchers(HttpMethod.GET, SecurityConstants.EMPLOYEES_PATTERN)
                        .hasAnyRole(SecurityConstants.ROLE_READER, SecurityConstants.ROLE_ADMIN)
                        .antMatchers(SecurityConstants.EMPLOYEES_PATTERN).hasRole(SecurityConstants.ROLE_ADMIN)
                        .anyRequest().denyAll())
                .httpBasic(basic -> basic
                        .realmName(SecurityConstants.REALM)
                        .authenticationEntryPoint(errorHandler))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                user(properties.reader(), encoder, SecurityConstants.ROLE_READER),
                user(properties.admin(), encoder, SecurityConstants.ROLE_ADMIN));
    }

    private static UserDetails user(SecurityProperties.Credentials credentials, PasswordEncoder encoder,
                                    String role) {
        return User.withUsername(credentials.username())
                .password(encoder.encode(credentials.password()))
                .roles(role)
                .build();
    }
}

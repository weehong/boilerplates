package com.example.boilerplate.shared.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String ACTUATOR_PATTERN = "/actuator/**";
    private static final String API_DOCS_PATTERN = "/v3/api-docs/**";
    private static final String SWAGGER_UI_PATTERN = "/swagger-ui/**";
    private static final String SWAGGER_HTML_PATTERN = "/swagger-ui.html";
    private static final String PUBLIC_API_PATTERN = "/api/v1/public/**";
    private static final String CORS_PATTERN = "/**";
    private static final String COMMA = ",";
    private static final long MAX_AGE_SECONDS = 3600L;

    private static final String[] PUBLIC_ENDPOINTS = {
        ACTUATOR_PATTERN,
        API_DOCS_PATTERN,
        SWAGGER_UI_PATTERN,
        SWAGGER_HTML_PATTERN,
        PUBLIC_API_PATTERN
    };

    private static final List<String> ALLOWED_METHODS = List.of(
        "GET",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "OPTIONS"
    );

    private static final List<String> ALLOWED_HEADERS = List.of(
        "Authorization",
        "Content-Type",
        "Accept",
        "Origin",
        "X-Requested-With",
        "Cache-Control"
    );

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated())
            // TODO: Uncomment and configure the JWT resource server to authenticate non-public endpoints
            // .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            Arrays.stream(allowedOrigins.split(COMMA))
                .map(String::trim)
                .toList());
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(MAX_AGE_SECONDS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CORS_PATTERN, configuration);

        return source;
    }

}

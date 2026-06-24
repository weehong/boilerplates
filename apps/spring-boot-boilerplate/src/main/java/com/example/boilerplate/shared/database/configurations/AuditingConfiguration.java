package com.example.boilerplate.shared.database.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfiguration {

    private static final String DEFAULT_AUDITOR = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO: Replace with actual user resolution from SecurityContext
        // Example:
        // return () -> Optional.ofNullable(SecurityContextHolder.getContext())
        //     .map(SecurityContext::getAuthentication)
        //     .filter(Authentication::isAuthenticated)
        //     .map(Authentication::getName);
        return () -> Optional.of(DEFAULT_AUDITOR);
    }

}

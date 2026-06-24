package com.example.boilerplate.shared.database.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AuditingConfigurationTest {

    private static final String SYSTEM_AUDITOR = "system";

    private final AuditingConfiguration configuration = new AuditingConfiguration();

    // ===== auditorAware =====

    @Test
    void given_noAuthenticatedUser_when_auditorAware_then_returnsSystemAuditor() {
        AuditorAware<String> auditorAware = configuration.auditorAware();
        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isEqualTo(SYSTEM_AUDITOR);
    }

}

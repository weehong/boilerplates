package com.example.boilerplate.shared.logging.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogAspectPropertiesTest {

    private static final Boolean ENABLED = true;
    private static final Long VALID_THRESHOLD_MS = 2000L;
    private static final Long DEFAULT_THRESHOLD_MS = 1000L;
    private static final Long NEGATIVE_THRESHOLD_MS = -500L;

    // ===== constructor =====

    @Test
    void given_validValues_when_constructed_then_valuesArePreserved() {
        LogAspectProperties properties =
            new LogAspectProperties(ENABLED, VALID_THRESHOLD_MS);

        assertThat(properties.enabled()).isTrue();
        assertThat(properties.slowExecutionThresholdMs()).isEqualTo(VALID_THRESHOLD_MS);
    }

    @Test
    void given_nullThreshold_when_constructed_then_defaultIsApplied() {
        LogAspectProperties properties =
            new LogAspectProperties(ENABLED, null);

        assertThat(properties.slowExecutionThresholdMs()).isEqualTo(DEFAULT_THRESHOLD_MS);
    }

    @Test
    void given_zeroThreshold_when_constructed_then_defaultIsApplied() {
        LogAspectProperties properties =
            new LogAspectProperties(ENABLED, 0L);

        assertThat(properties.slowExecutionThresholdMs()).isEqualTo(DEFAULT_THRESHOLD_MS);
    }

    @Test
    void given_negativeThreshold_when_constructed_then_defaultIsApplied() {
        LogAspectProperties properties =
            new LogAspectProperties(ENABLED, NEGATIVE_THRESHOLD_MS);

        assertThat(properties.slowExecutionThresholdMs()).isEqualTo(DEFAULT_THRESHOLD_MS);
    }

}

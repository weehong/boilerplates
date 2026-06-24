package com.example.boilerplate.shared.database.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HikariPropertiesTest {

    private static final Integer MAXIMUM_POOL_SIZE = 10;
    private static final Integer MINIMUM_IDLE = 2;
    private static final Long IDLE_TIMEOUT = 300000L;
    private static final Long MAX_LIFETIME = 1800000L;
    private static final Long CONNECTION_TIMEOUT = 30000L;
    private static final Long LEAK_DETECTION_THRESHOLD = 60000L;
    private static final Long INITIALIZATION_FAIL_TIMEOUT = 30000L;

    // ===== constructor =====

    @Test
    void given_validValues_when_constructed_then_valuesArePreserved() {
        HikariProperties properties = new HikariProperties(
            MAXIMUM_POOL_SIZE,
            MINIMUM_IDLE,
            IDLE_TIMEOUT,
            MAX_LIFETIME,
            CONNECTION_TIMEOUT,
            LEAK_DETECTION_THRESHOLD,
            INITIALIZATION_FAIL_TIMEOUT);

        assertThat(properties.maximumPoolSize()).isEqualTo(MAXIMUM_POOL_SIZE);
        assertThat(properties.minimumIdle()).isEqualTo(MINIMUM_IDLE);
        assertThat(properties.idleTimeout()).isEqualTo(IDLE_TIMEOUT);
        assertThat(properties.maxLifetime()).isEqualTo(MAX_LIFETIME);
        assertThat(properties.connectionTimeout()).isEqualTo(CONNECTION_TIMEOUT);
        assertThat(properties.leakDetectionThreshold()).isEqualTo(LEAK_DETECTION_THRESHOLD);
        assertThat(properties.initializationFailTimeout()).isEqualTo(INITIALIZATION_FAIL_TIMEOUT);
    }

    @Test
    void given_nullValues_when_constructed_then_valuesAreNull() {
        HikariProperties properties = new HikariProperties(
            null,
            null,
            null,
            null,
            null,
            null,
            null);

        assertThat(properties.maximumPoolSize()).isNull();
        assertThat(properties.minimumIdle()).isNull();
        assertThat(properties.idleTimeout()).isNull();
        assertThat(properties.maxLifetime()).isNull();
        assertThat(properties.connectionTimeout()).isNull();
        assertThat(properties.leakDetectionThreshold()).isNull();
        assertThat(properties.initializationFailTimeout()).isNull();
    }

}

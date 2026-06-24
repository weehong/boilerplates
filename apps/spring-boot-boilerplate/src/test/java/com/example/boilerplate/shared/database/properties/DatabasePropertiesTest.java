package com.example.boilerplate.shared.database.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabasePropertiesTest {

    private static final String URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "secret";
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final Integer HIKARI_MAX_POOL_SIZE = 10;
    private static final Integer HIKARI_MIN_IDLE = 2;
    private static final Long HIKARI_IDLE_TIMEOUT = 300000L;
    private static final Long HIKARI_MAX_LIFETIME = 1800000L;
    private static final Long HIKARI_CONNECTION_TIMEOUT = 30000L;
    private static final Long HIKARI_LEAK_DETECTION = 60000L;
    private static final Long HIKARI_INIT_FAIL_TIMEOUT = 30000L;

    // ===== constructor =====

    @Test
    void given_validValues_when_constructed_then_valuesArePreserved() {
        HikariProperties hikari = new HikariProperties(HIKARI_MAX_POOL_SIZE,
                                                       HIKARI_MIN_IDLE,
                                                       HIKARI_IDLE_TIMEOUT,
                                                       HIKARI_MAX_LIFETIME,
                                                       HIKARI_CONNECTION_TIMEOUT,
                                                       HIKARI_LEAK_DETECTION,
                                                       HIKARI_INIT_FAIL_TIMEOUT);
        DatabaseProperties properties = new DatabaseProperties(
            URL,
            USERNAME,
            PASSWORD,
            DRIVER_CLASS_NAME,
            hikari);

        assertThat(properties.url()).isEqualTo(URL);
        assertThat(properties.username()).isEqualTo(USERNAME);
        assertThat(properties.password()).isEqualTo(PASSWORD);
        assertThat(properties.driverClassName()).isEqualTo(DRIVER_CLASS_NAME);
        assertThat(properties.hikari()).isEqualTo(hikari);
    }

    @Test
    void given_nullHikari_when_constructed_then_hikariIsNull() {
        DatabaseProperties properties = new DatabaseProperties(
            URL,
            USERNAME,
            PASSWORD,
            DRIVER_CLASS_NAME,
            null);

        assertThat(properties.hikari()).isNull();
    }

    @Test
    void given_nullDriverClassName_when_constructed_then_driverClassNameIsNull() {
        DatabaseProperties properties = new DatabaseProperties(
            URL,
            USERNAME,
            PASSWORD,
            null,
            null);

        assertThat(properties.driverClassName()).isNull();
    }

}

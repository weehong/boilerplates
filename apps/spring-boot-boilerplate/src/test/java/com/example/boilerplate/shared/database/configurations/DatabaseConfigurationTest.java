package com.example.boilerplate.shared.database.configurations;

import com.example.boilerplate.shared.database.properties.DatabaseProperties;
import com.example.boilerplate.shared.database.properties.HikariProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseConfigurationTest {

    private static final String H2_URL = "jdbc:h2:mem:testdb";
    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String UNSUPPORTED_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static final String SPRING_APP_NAME = "SpringBootBoilerplate";
    private static final String[] EMPTY_PROFILES = new String[]{ };
    private static final String[] ACTIVE_PROFILES = new String[]{ "test" };
    private static final Integer HIKARI_MAX_POOL_SIZE = 5;
    private static final Integer HIKARI_MIN_IDLE = 1;
    private static final Long HIKARI_IDLE_TIMEOUT = 60000L;
    private static final Long HIKARI_MAX_LIFETIME = 120000L;
    private static final Long HIKARI_CONNECTION_TIMEOUT = 10000L;
    private static final Long HIKARI_LEAK_DETECTION = 5000L;
    private static final Long HIKARI_INIT_FAIL_TIMEOUT = 10000L;

    @Mock
    private Environment environment;

    // ===== dataSource =====

    @Test
    void given_h2Properties_when_dataSource_then_returnsDataSource() {
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            null,
            null);
        when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(SPRING_APP_NAME);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

    @Test
    void given_h2PropertiesWithHikari_when_dataSource_then_returnsDataSource() {
        HikariProperties hikari = new HikariProperties(
            HIKARI_MAX_POOL_SIZE,
            HIKARI_MIN_IDLE,
            HIKARI_IDLE_TIMEOUT,
            HIKARI_MAX_LIFETIME,
            HIKARI_CONNECTION_TIMEOUT,
            HIKARI_LEAK_DETECTION,
            HIKARI_INIT_FAIL_TIMEOUT);
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            null,
            hikari);
        when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(SPRING_APP_NAME);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

    @Test
    void given_h2PropertiesWithMeterRegistry_when_dataSource_then_returnsDataSource() {
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            null,
            null);
        when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(SPRING_APP_NAME);
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, meterRegistry);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

    @Test
    void given_h2PropertiesWithActiveProfiles_when_dataSource_then_returnsDataSource() {
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            null,
            null);
        when(environment.getActiveProfiles()).thenReturn(ACTIVE_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(SPRING_APP_NAME);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

    @Test
    void given_h2PropertiesWithExplicitDriver_when_dataSource_then_returnsDataSource() {
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            "org.h2.Driver",
            null);
        when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(SPRING_APP_NAME);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

    @Test
    void given_nullUrl_when_dataSource_then_throwsIllegalStateException() {
        DatabaseProperties properties = new DatabaseProperties(
            null,
            USERNAME,
            PASSWORD,
            null,
            null);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        assertThatThrownBy(configuration::dataSource)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Database URL must be configured");
    }

    @Test
    void given_emptyUrl_when_dataSource_then_throwsIllegalStateException() {
        DatabaseProperties properties = new DatabaseProperties(
            "",
            USERNAME,
            PASSWORD,
            null,
            null);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        assertThatThrownBy(configuration::dataSource)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Database URL must be configured");
    }

    @Test
    void given_unsupportedUrl_when_dataSource_then_throwsIllegalArgumentException() {
        DatabaseProperties properties = new DatabaseProperties(
            UNSUPPORTED_URL,
            USERNAME,
            PASSWORD,
            null,
            null);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        assertThatThrownBy(configuration::dataSource)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported JDBC URL");
    }

    @Test
    void given_noSpringAppName_when_dataSource_then_returnsDataSource() {
        DatabaseProperties properties = new DatabaseProperties(
            H2_URL,
            USERNAME,
            PASSWORD,
            null,
            null);
        when(environment.getActiveProfiles()).thenReturn(EMPTY_PROFILES);
        when(environment.getProperty("spring.application.name")).thenReturn(null);
        DatabaseConfiguration configuration = new DatabaseConfiguration(properties, environment, null);

        DataSource dataSource = configuration.dataSource();

        assertThat(dataSource).isNotNull();
    }

}

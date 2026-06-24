package com.example.boilerplate.shared.database.configurations;

import com.example.boilerplate.shared.database.enums.DatabaseType;
import com.example.boilerplate.shared.database.properties.DatabaseProperties;
import com.example.boilerplate.shared.database.properties.HikariProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_APPLICATION_NAME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_CONNECTION_TIMEOUT;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_IDLE_TIMEOUT;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_INITIALIZATION_FAIL_TIMEOUT;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_MAXIMUM_POOL_SIZE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_MAX_LIFETIME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_MINIMUM_IDLE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.DEFAULT_POOL_NAME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_CACHE_SIZE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_CACHE_SIZE_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_DB_CLOSE_DELAY;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_DB_CLOSE_DELAY_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_DB_CLOSE_ON_EXIT;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_DB_CLOSE_ON_EXIT_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_LOCK_TIMEOUT;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.H2_LOCK_TIMEOUT_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.JDBC_H2;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.JDBC_MYSQL;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.JDBC_POSTGRESQL;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LEAK_DETECTION_DISABLED;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_APPLICATION_NAME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_DATABASE_URL;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_DRIVER;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_H2_OPTIMIZATIONS;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_HIKARI_CONFIGURED;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_INITIALIZING_DATASOURCE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_LEAK_DETECTION;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_METRICS_ENABLED;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_POOL_NAME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_POOL_SIZE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_TIMEOUTS;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.LOG_UNSUPPORTED_JDBC_URL;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.NOT_AVAILABLE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_APPLICATION_NAME;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_ASSUME_MIN_SERVER_VERSION;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_ASSUME_MIN_SERVER_VERSION_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_MAINTAIN_TIME_STATS;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARED_STATEMENT_CACHE_QUERIES;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARED_STATEMENT_CACHE_QUERIES_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARED_STATEMENT_CACHE_SIZE_MIB;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARED_STATEMENT_CACHE_SIZE_MIB_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARE_THRESHOLD;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_PREPARE_THRESHOLD_VALUE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_REWRITE_BATCHED_INSERTS;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PG_TCP_KEEP_ALIVE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.PROFILE_SEPARATOR;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.SPRING_APPLICATION_NAME_PROPERTY;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.UNIT_MS;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.UNKNOWN_DATABASE_TYPE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.URL_CREDENTIALS_MASK;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.URL_CREDENTIALS_PATTERN;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.URL_PASSWORD_MASK;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.URL_PASSWORD_PATTERN;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.VALUE_FALSE;
import static com.example.boilerplate.shared.database.constants.DatabaseConstant.VALUE_TRUE;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DatabaseProperties.class)
public final class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final DatabaseProperties databaseProperties;
    private final Environment environment;
    private final MeterRegistry meterRegistry;

    public DatabaseConfiguration(
        DatabaseProperties databaseProperties,
        Environment environment,
        @Nullable MeterRegistry meterRegistry) {
        this.databaseProperties = databaseProperties;
        this.environment = environment;
        this.meterRegistry = meterRegistry;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "app.database", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DataSource dataSource() {
        String jdbcUrl = databaseProperties.url();

        if (!StringUtils.hasText(jdbcUrl)) {
            throw new IllegalStateException("Database URL must be configured");
        }

        String resolvedDriverClassName = resolveDriverClassName(jdbcUrl);
        String databaseTypeName = getDatabaseType(jdbcUrl);

        LOGGER.info(LOG_INITIALIZING_DATASOURCE, databaseTypeName);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseProperties.username());
        hikariConfig.setPassword(databaseProperties.password());
        hikariConfig.setDriverClassName(resolvedDriverClassName);
        hikariConfig.setPoolName(resolvePoolName());
        hikariConfig.setAutoCommit(false);
        hikariConfig.setReadOnly(false);
        applyPoolSizing(hikariConfig);
        applyTimeouts(hikariConfig);
        String applicationName = resolveApplicationName();
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);

        if (databaseType == DatabaseType.POSTGRESQL) {
            applyPostgreSQLOptimizations(hikariConfig, applicationName);
        } else if (databaseType == DatabaseType.H2) {
            applyH2Optimizations(hikariConfig);
        }

        applyMonitoring(hikariConfig);
        logConfigurationSummary(hikariConfig, resolvedDriverClassName, applicationName);

        return new HikariDataSource(hikariConfig);
    }

    private String resolveDriverClassName(String jdbcUrl) {
        String configuredDriver = databaseProperties.driverClassName();

        if (StringUtils.hasText(configuredDriver)) {
            return configuredDriver;
        }

        return getDriverClassName(jdbcUrl);
    }

    private String getDriverClassName(String jdbcUrl) {
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);

        if (databaseType == null) {
            LOGGER.error(
                LOG_UNSUPPORTED_JDBC_URL,
                maskSensitiveUrl(jdbcUrl),
                JDBC_H2,
                JDBC_POSTGRESQL,
                JDBC_MYSQL);

            throw new IllegalArgumentException(
                String.format(
                    "Unsupported JDBC URL. Supported prefixes are: %s, %s, %s",
                    JDBC_H2,
                    JDBC_POSTGRESQL,
                    JDBC_MYSQL));
        }

        return databaseType.getDriverClassName();
    }

    private String getDatabaseType(String jdbcUrl) {
        DatabaseType databaseType = DatabaseType.fromJdbcUrl(jdbcUrl);

        return databaseType != null
               ? databaseType.name()
               : UNKNOWN_DATABASE_TYPE;
    }

    private String resolvePoolName() {
        String[] profiles = environment.getActiveProfiles();

        return profiles.length > 0
               ? DEFAULT_POOL_NAME + PROFILE_SEPARATOR + String.join(PROFILE_SEPARATOR, profiles)
               : DEFAULT_POOL_NAME;
    }

    private void applyPoolSizing(HikariConfig config) {
        config.setMaximumPoolSize(getMaximumPoolSize());
        config.setMinimumIdle(getMinimumIdle());
    }

    private int getMaximumPoolSize() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.maximumPoolSize() == null) {
            return DEFAULT_MAXIMUM_POOL_SIZE;
        }

        return hikari.maximumPoolSize();
    }

    private int getMinimumIdle() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.minimumIdle() == null) {
            return DEFAULT_MINIMUM_IDLE;
        }

        return hikari.minimumIdle();
    }

    private void applyTimeouts(HikariConfig hikariConfig) {
        hikariConfig.setConnectionTimeout(getConnectionTimeout());
        hikariConfig.setIdleTimeout(getIdleTimeout());
        hikariConfig.setMaxLifetime(getMaxLifetime());
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari != null && hikari.leakDetectionThreshold() != null
            && hikari.leakDetectionThreshold() > 0) {
            hikariConfig.setLeakDetectionThreshold(hikari.leakDetectionThreshold());
        }

        hikariConfig.setInitializationFailTimeout(getInitializationFailTimeout());
    }

    private long getInitializationFailTimeout() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.initializationFailTimeout() == null) {
            return DEFAULT_INITIALIZATION_FAIL_TIMEOUT;
        }

        return hikari.initializationFailTimeout();
    }

    private long getConnectionTimeout() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.connectionTimeout() == null) {
            return DEFAULT_CONNECTION_TIMEOUT;
        }

        return hikari.connectionTimeout();
    }

    private long getIdleTimeout() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.idleTimeout() == null) {
            return DEFAULT_IDLE_TIMEOUT;
        }

        return hikari.idleTimeout();
    }

    private long getMaxLifetime() {
        HikariProperties hikari = databaseProperties.hikari();

        if (hikari == null || hikari.maxLifetime() == null) {
            return DEFAULT_MAX_LIFETIME;
        }

        return hikari.maxLifetime();
    }

    private void applyPostgreSQLOptimizations(HikariConfig hikariConfig, String applicationName) {
        hikariConfig.addDataSourceProperty(PG_TCP_KEEP_ALIVE, VALUE_TRUE);
        hikariConfig.addDataSourceProperty(PG_APPLICATION_NAME, applicationName);
        hikariConfig.addDataSourceProperty(PG_ASSUME_MIN_SERVER_VERSION, PG_ASSUME_MIN_SERVER_VERSION_VALUE);
        hikariConfig.addDataSourceProperty(PG_REWRITE_BATCHED_INSERTS, VALUE_TRUE);
        hikariConfig.addDataSourceProperty(PG_PREPARE_THRESHOLD, PG_PREPARE_THRESHOLD_VALUE);
        hikariConfig.addDataSourceProperty(PG_PREPARED_STATEMENT_CACHE_QUERIES,
                                           PG_PREPARED_STATEMENT_CACHE_QUERIES_VALUE);
        hikariConfig.addDataSourceProperty(PG_PREPARED_STATEMENT_CACHE_SIZE_MIB,
                                           PG_PREPARED_STATEMENT_CACHE_SIZE_MIB_VALUE);
        hikariConfig.addDataSourceProperty(PG_MAINTAIN_TIME_STATS, VALUE_FALSE);
    }

    private String resolveApplicationName() {
        String springAppName = environment.getProperty(SPRING_APPLICATION_NAME_PROPERTY);

        if (StringUtils.hasText(springAppName)) {
            String[] profiles = environment.getActiveProfiles();

            return profiles.length > 0
                   ? springAppName + PROFILE_SEPARATOR + String.join(PROFILE_SEPARATOR, profiles)
                   : springAppName;
        }

        return DEFAULT_APPLICATION_NAME;
    }

    private void applyH2Optimizations(HikariConfig hikariConfig) {
        hikariConfig.addDataSourceProperty(H2_DB_CLOSE_DELAY, H2_DB_CLOSE_DELAY_VALUE);
        hikariConfig.addDataSourceProperty(H2_DB_CLOSE_ON_EXIT, H2_DB_CLOSE_ON_EXIT_VALUE);
        hikariConfig.addDataSourceProperty(H2_CACHE_SIZE, H2_CACHE_SIZE_VALUE);
        hikariConfig.addDataSourceProperty(H2_LOCK_TIMEOUT, H2_LOCK_TIMEOUT_VALUE);
        LOGGER.debug(LOG_H2_OPTIMIZATIONS);
    }

    private void applyMonitoring(HikariConfig config) {
        config.setRegisterMbeans(false);

        if (meterRegistry != null) {
            config.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(meterRegistry));
            LOGGER.info(LOG_METRICS_ENABLED, meterRegistry.getClass().getSimpleName());
        }
    }

    private void logConfigurationSummary(HikariConfig config, String driver, String applicationName) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LOG_HIKARI_CONFIGURED);
            LOGGER.info(LOG_POOL_NAME, config.getPoolName());
            LOGGER.info(
                LOG_POOL_SIZE,
                config.getMinimumIdle(),
                config.getMaximumPoolSize());
            LOGGER.info(
                LOG_TIMEOUTS,
                config.getConnectionTimeout(),
                config.getIdleTimeout(),
                config.getMaxLifetime());
            LOGGER.info(
                LOG_LEAK_DETECTION,
                config.getLeakDetectionThreshold() > 0
                ? config.getLeakDetectionThreshold() + UNIT_MS
                : LEAK_DETECTION_DISABLED);
            LOGGER.info(LOG_DATABASE_URL, maskSensitiveUrl(databaseProperties.url()));
            LOGGER.info(LOG_DRIVER, driver);
            LOGGER.info(LOG_APPLICATION_NAME, applicationName);
        }
    }

    private String maskSensitiveUrl(String url) {

        if (!StringUtils.hasText(url)) {
            return NOT_AVAILABLE;
        }

        return url.replaceAll(URL_CREDENTIALS_PATTERN, URL_CREDENTIALS_MASK)
                  .replaceAll(URL_PASSWORD_PATTERN, URL_PASSWORD_MASK);
    }

}

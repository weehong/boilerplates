package com.example.boilerplate.shared.database.constants;

import java.util.concurrent.TimeUnit;

public final class DatabaseConstant {

    public static final String JDBC_H2 = "jdbc:h2:";
    public static final String JDBC_POSTGRESQL = "jdbc:postgresql:";
    public static final String JDBC_MYSQL = "jdbc:mysql:";
    public static final String DEFAULT_POOL_NAME = "hikari-pool";
    public static final String DEFAULT_APPLICATION_NAME = "SpringBootBoilerplate";
    public static final int DEFAULT_MAXIMUM_POOL_SIZE = 10;
    public static final int DEFAULT_MINIMUM_IDLE = 10;
    public static final long DEFAULT_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    public static final long DEFAULT_IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    public static final long DEFAULT_MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    public static final long DEFAULT_INITIALIZATION_FAIL_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    public static final String PROFILE_SEPARATOR = "-";
    public static final String UNKNOWN_DATABASE_TYPE = "Unknown";
    public static final String NOT_AVAILABLE = "N/A";
    public static final String SPRING_APPLICATION_NAME_PROPERTY = "spring.application.name";
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String LOG_INITIALIZING_DATASOURCE = "Initializing HikariCP DataSource for {}";
    public static final String LOG_UNSUPPORTED_JDBC_URL = "Unsupported JDBC URL: {}. Supported prefixes are: {}, {}, "
                                                          + "{}";
    public static final String LOG_H2_OPTIMIZATIONS = "Applied H2-specific optimizations";
    public static final String LOG_METRICS_ENABLED = "Metrics enabled with {}";
    public static final String LOG_HIKARI_CONFIGURED = "HikariCP configured:";
    public static final String LOG_POOL_NAME = "   Pool Name: {}";
    public static final String LOG_POOL_SIZE = "   Pool Size: min={}, max={}";
    public static final String LOG_TIMEOUTS = "   Timeouts: connection={}ms, idle={}ms, maxLifetime={}ms";
    public static final String LOG_LEAK_DETECTION = "   Leak Detection: {}";
    public static final String LOG_DATABASE_URL = "   Database URL: {}";
    public static final String LOG_DRIVER = "   Driver: {}";
    public static final String LOG_APPLICATION_NAME = "   Application Name: {}";
    public static final String UNIT_MS = "ms";
    public static final String LEAK_DETECTION_DISABLED = "disabled";
    public static final String PG_TCP_KEEP_ALIVE = "tcpKeepAlive";
    public static final String PG_APPLICATION_NAME = "ApplicationName";
    public static final String PG_ASSUME_MIN_SERVER_VERSION = "assumeMinServerVersion";
    public static final String PG_ASSUME_MIN_SERVER_VERSION_VALUE = "12.0";
    public static final String PG_REWRITE_BATCHED_INSERTS = "reWriteBatchedInserts";
    public static final String PG_PREPARE_THRESHOLD = "prepareThreshold";
    public static final String PG_PREPARE_THRESHOLD_VALUE = "5";
    public static final String PG_PREPARED_STATEMENT_CACHE_QUERIES = "preparedStatementCacheQueries";
    public static final String PG_PREPARED_STATEMENT_CACHE_QUERIES_VALUE = "250";
    public static final String PG_PREPARED_STATEMENT_CACHE_SIZE_MIB = "preparedStatementCacheSizeMiB";
    public static final String PG_PREPARED_STATEMENT_CACHE_SIZE_MIB_VALUE = "5";
    public static final String PG_MAINTAIN_TIME_STATS = "maintainTimeStats";
    public static final String H2_DB_CLOSE_DELAY = "DB_CLOSE_DELAY";
    public static final String H2_DB_CLOSE_DELAY_VALUE = "-1";
    public static final String H2_DB_CLOSE_ON_EXIT = "DB_CLOSE_ON_EXIT";
    public static final String H2_DB_CLOSE_ON_EXIT_VALUE = "FALSE";
    public static final String H2_CACHE_SIZE = "CACHE_SIZE";
    public static final String H2_CACHE_SIZE_VALUE = "65536";
    public static final String H2_LOCK_TIMEOUT = "LOCK_TIMEOUT";
    public static final String H2_LOCK_TIMEOUT_VALUE = "10000";
    public static final String URL_CREDENTIALS_PATTERN = "://[^@/]*@";
    public static final String URL_CREDENTIALS_MASK = "://***:***@";
    public static final String URL_PASSWORD_PATTERN = "([?&]password=)[^&]*";
    public static final String URL_PASSWORD_MASK = "$1***";

    private DatabaseConstant() {
        throw new AssertionError();
    }

}

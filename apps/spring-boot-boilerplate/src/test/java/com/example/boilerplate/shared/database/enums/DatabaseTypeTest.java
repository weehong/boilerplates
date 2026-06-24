package com.example.boilerplate.shared.database.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseTypeTest {

    private static final String H2_URL = "jdbc:h2:mem:testdb";
    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String UNSUPPORTED_URL = "jdbc:oracle:thin:@localhost:1521:xe";

    // ===== fromJdbcUrl =====

    @Test
    void given_h2Url_when_fromJdbcUrl_then_returnsH2() {
        DatabaseType result = DatabaseType.fromJdbcUrl(H2_URL);

        assertThat(result).isEqualTo(DatabaseType.H2);
    }

    @Test
    void given_postgresqlUrl_when_fromJdbcUrl_then_returnsPostgresql() {
        DatabaseType result = DatabaseType.fromJdbcUrl(POSTGRESQL_URL);

        assertThat(result).isEqualTo(DatabaseType.POSTGRESQL);
    }

    @Test
    void given_mysqlUrl_when_fromJdbcUrl_then_returnsMysql() {
        DatabaseType result = DatabaseType.fromJdbcUrl(MYSQL_URL);

        assertThat(result).isEqualTo(DatabaseType.MYSQL);
    }

    @Test
    void given_unsupportedUrl_when_fromJdbcUrl_then_returnsNull() {
        DatabaseType result = DatabaseType.fromJdbcUrl(UNSUPPORTED_URL);

        assertThat(result).isNull();
    }

    @Test
    void given_nullUrl_when_fromJdbcUrl_then_returnsNull() {
        DatabaseType result = DatabaseType.fromJdbcUrl(null);

        assertThat(result).isNull();
    }

    // ===== getPrefix =====

    @Test
    void given_postgresql_when_getPrefix_then_returnsJdbcPrefix() {
        assertThat(DatabaseType.POSTGRESQL.getPrefix()).isEqualTo("jdbc:postgresql:");
    }

    // ===== getDriverClassName =====

    @Test
    void given_postgresql_when_getDriverClassName_then_returnsDriverClass() {
        assertThat(DatabaseType.POSTGRESQL.getDriverClassName())
            .isEqualTo("org.postgresql.Driver");
    }

    @Test
    void given_h2_when_getDriverClassName_then_returnsDriverClass() {
        assertThat(DatabaseType.H2.getDriverClassName())
            .isEqualTo("org.h2.Driver");
    }

    @Test
    void given_mysql_when_getDriverClassName_then_returnsDriverClass() {
        assertThat(DatabaseType.MYSQL.getDriverClassName())
            .isEqualTo("com.mysql.cj.jdbc.Driver");
    }

}

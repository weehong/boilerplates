package com.example.boilerplate.shared.database.properties;

public record HikariProperties(
    Integer maximumPoolSize,
    Integer minimumIdle,
    Long idleTimeout,
    Long maxLifetime,
    Long connectionTimeout,
    Long leakDetectionThreshold,
    Long initializationFailTimeout) {

}

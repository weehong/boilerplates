package com.example.boilerplate.shared.database.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.datasource")
public record DatabaseProperties(
    String url,
    String username,
    String password,
    String driverClassName,
    @NestedConfigurationProperty HikariProperties hikari) {

}

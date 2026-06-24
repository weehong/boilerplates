package com.example.boilerplate.shared.metrics.configurations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MetricsConfiguration {

    private static final String TAG_APPLICATION = "application";
    private static final String PROPERTY_APP_NAME = "spring.application.name";
    private static final String DEFAULT_APP_NAME = "SpringBootBoilerplate";

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        return registry -> registry.config().commonTags(
            TAG_APPLICATION, environment.getProperty(PROPERTY_APP_NAME, DEFAULT_APP_NAME));
    }

    @Bean
    public LogbackMetrics logbackMetrics() {
        return new LogbackMetrics();
    }

}

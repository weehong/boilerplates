package com.example.boilerplate.shared.metrics.configurations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class MetricsConfigurationTest {

    @Mock
    private Environment environment;

    private MetricsConfiguration metricsConfiguration;

    @BeforeEach
    void setUp() {
        metricsConfiguration = new MetricsConfiguration();
    }

    // ===== metricsCommonTags =====

    @Test
    void given_environment_when_metricsCommonTags_then_returnsCustomizer() {
        MeterRegistryCustomizer<MeterRegistry> customizer =
            metricsConfiguration.metricsCommonTags(environment);

        assertThat(customizer).isNotNull();
    }

    // ===== logbackMetrics =====

    @Test
    void given_configuration_when_logbackMetrics_then_returnsInstance() {
        LogbackMetrics logbackMetrics = metricsConfiguration.logbackMetrics();

        assertThat(logbackMetrics).isNotNull();
    }

}

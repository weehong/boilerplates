package com.example.boilerplate.shared.logging.properties;

import com.example.boilerplate.shared.logging.constants.AspectConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logging.aspect")
public record LogAspectProperties(Boolean enabled, Long slowExecutionThresholdMs) {

    public LogAspectProperties {
        if (enabled == null) {
            enabled = AspectConstant.DEFAULT_ENABLED;
        }

        if (slowExecutionThresholdMs == null || slowExecutionThresholdMs <= 0) {
            slowExecutionThresholdMs = AspectConstant.SLOW_EXECUTION_THRESHOLD_MS;
        }
    }

}

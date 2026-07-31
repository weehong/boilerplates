package com.example.boilerplate.shared.tracing.configurations;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfiguration {

    /**
     * Registers the W3C propagators with the OpenTelemetry SDK.
     *
     * <p>Without this bean the SDK is created with an empty {@code ContextPropagators},
     * so the {@code Propagator} reports no fields and an inbound {@code traceparent}
     * is silently discarded and replaced by a new trace. Log correlation across
     * services depends on this being present.
     */
    @Bean
    @ConditionalOnMissingBean
    public ContextPropagators contextPropagators() {
        return ContextPropagators.create(
            TextMapPropagator.composite(
                W3CTraceContextPropagator.getInstance(),
                W3CBaggagePropagator.getInstance()));
    }

}

package com.example.boilerplate.shared.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigurationTest {

    private final JacksonConfiguration configuration = new JacksonConfiguration();

    // ===== objectMapper =====

    @Test
    void given_objectMapper_when_configured_then_datesAreNotWrittenAsTimestamps() {
        ObjectMapper mapper = configuration.objectMapper();

        assertThat(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
    }

    @Test
    void given_instant_when_serialized_then_producesIsoString() throws JsonProcessingException {
        ObjectMapper mapper = configuration.objectMapper();
        Instant now = Instant.parse("2026-01-15T10:30:00Z");

        String json = mapper.writeValueAsString(now);

        assertThat(json).contains("2026-01-15");
        assertThat(json).doesNotContain("1768473000");
    }

    @Test
    void given_unknownProperties_when_deserialized_then_ignored() throws JsonProcessingException {
        ObjectMapper mapper = configuration.objectMapper();
        String json = "{\"known\":\"value\",\"unknown\":\"ignored\"}";

        record TestRecord(String known) { }

        TestRecord result = mapper.readValue(json, TestRecord.class);
        assertThat(result.known()).isEqualTo("value");
    }

}

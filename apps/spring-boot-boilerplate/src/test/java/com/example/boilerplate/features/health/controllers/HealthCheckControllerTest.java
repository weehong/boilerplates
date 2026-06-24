package com.example.boilerplate.features.health.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.example.boilerplate.shared.configurations.SecurityConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
@Import(SecurityConfiguration.class)
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== healthCheck =====

    @Test
    void given_applicationRunning_when_healthCheckRequested_then_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/public/health-checks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("OK"))
            .andExpect(jsonPath("$.message").value("Service is running"));
    }

}

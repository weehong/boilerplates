package com.example.boilerplate.shared.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String PUBLIC_HEALTH_ENDPOINT = "/api/v1/public/health-checks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    // ===== securityFilterChain =====

    @Test
    void given_applicationContext_when_loaded_then_securityFilterChainBeanExists() {
        assertThat(securityFilterChain).isNotNull();
    }

    // ===== corsConfigurationSource =====

    @Test
    void given_applicationContext_when_loaded_then_corsConfigurationSourceBeanExists() {
        assertThat(corsConfigurationSource).isNotNull();
    }

    // ===== authorizeHttpRequests =====

    @Test
    void given_actuatorEndpoint_when_requested_then_accessible() throws Exception {
        mockMvc.perform(get(ACTUATOR_HEALTH_ENDPOINT))
            .andExpect(status().isOk());
    }

    @Test
    void given_publicEndpoint_when_requested_then_accessible() throws Exception {
        mockMvc.perform(get(PUBLIC_HEALTH_ENDPOINT))
            .andExpect(status().isOk());
    }

}

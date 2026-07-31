package com.example.boilerplate.shared.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMetrics
@Import(SecurityConfigurationTest.JsonContractController.class)
class SecurityConfigurationTest {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final String SCRAPER_USER = "scraper";

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String ACTUATOR_LIVENESS_ENDPOINT = "/actuator/health/liveness";
    private static final String ACTUATOR_READINESS_ENDPOINT = "/actuator/health/readiness";
    private static final String ACTUATOR_ROOT_ENDPOINT = "/actuator";
    private static final String PROMETHEUS_ENDPOINT = "/actuator/prometheus";
    private static final String PUBLIC_HEALTH_ENDPOINT = "/api/v1/public/health-checks";
    private static final String JSON_CONTRACT_ENDPOINT = "/api/v1/public/json-contract";

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
    void given_actuatorHealthEndpoint_when_requestedAnonymously_then_accessible() throws Exception {
        mockMvc.perform(get(ACTUATOR_HEALTH_ENDPOINT))
            .andExpect(status().isOk());
    }

    @Test
    void given_livenessEndpoint_when_requestedAnonymously_then_accessible() throws Exception {
        mockMvc.perform(get(ACTUATOR_LIVENESS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void given_readinessEndpoint_when_requestedAnonymously_then_accessible() throws Exception {
        mockMvc.perform(get(ACTUATOR_READINESS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void given_actuatorRoot_when_requestedAnonymously_then_accessIsRefused() throws Exception {
        mockMvc.perform(get(ACTUATOR_ROOT_ENDPOINT))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_prometheusEndpoint_when_requestedAnonymously_then_accessIsRefused() throws Exception {
        mockMvc.perform(get(PROMETHEUS_ENDPOINT))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_prometheusEndpoint_when_requestedByAuthenticatedUser_then_realMetricsAreServed()
        throws Exception {
        mockMvc.perform(get(PROMETHEUS_ENDPOINT).with(user(SCRAPER_USER)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("# HELP")))
            .andExpect(content().string(containsString("jvm_")))
            .andExpect(content().string(containsString("application=\"SpringBootBoilerplate\"")));
    }

    @Test
    void given_publicEndpoint_when_requested_then_accessible() throws Exception {
        mockMvc.perform(get(PUBLIC_HEALTH_ENDPOINT))
            .andExpect(status().isOk());
    }

    @Test
    void given_unknownRequestField_when_posted_then_structuredBadRequestIsReturned() throws Exception {
        mockMvc.perform(post(JSON_CONTRACT_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"displayName\":\"Ada\",\"unexpectedField\":\"rejected\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(HTTP_BAD_REQUEST))
            .andExpect(jsonPath("$.detail").value("Failed to read request"))
            .andExpect(jsonPath("$.instance").value(JSON_CONTRACT_ENDPOINT))
            .andExpect(content().string(not(containsString("unexpectedField"))));
    }

    @Test
    void given_camelCaseRequestField_when_posted_then_existingPropertyNamingIsPreserved() throws Exception {
        mockMvc.perform(post(JSON_CONTRACT_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"displayName\":\"Ada\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName").value("Ada"))
            .andExpect(jsonPath("$.display_name").doesNotExist());
    }

    @RestController
    @RequestMapping(JSON_CONTRACT_ENDPOINT)
    static class JsonContractController {

        @PostMapping
        JsonContract echo(@RequestBody JsonContract request) {
            return request;
        }
    }

    record JsonContract(String displayName) {
    }

}

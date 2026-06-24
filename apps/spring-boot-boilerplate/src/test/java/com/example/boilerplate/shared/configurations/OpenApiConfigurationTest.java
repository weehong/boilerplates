package com.example.boilerplate.shared.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigurationTest {

    private static final int TEST_SERVER_PORT = 9090;
    private static final String EXPECTED_TITLE = "SpringBootBoilerplate API";
    private static final String EXPECTED_VERSION = "0.0.1";
    private static final String EXPECTED_SERVER_URL =
        "http://localhost:" + TEST_SERVER_PORT;

    private OpenApiConfiguration openApiConfiguration;

    @BeforeEach
    void setUp() throws Exception {
        openApiConfiguration = new OpenApiConfiguration();
        Field serverPortField =
            OpenApiConfiguration.class.getDeclaredField("serverPort");
        serverPortField.setAccessible(true);
        serverPortField.setInt(openApiConfiguration, TEST_SERVER_PORT);
    }

    // ===== openApi =====

    @Test
    void given_configuration_when_openApi_then_returnsOpenApiInstance() {
        OpenAPI openApi = openApiConfiguration.openApi();

        assertThat(openApi).isNotNull();
    }

    @Test
    void given_configuration_when_openApi_then_infoIsPopulated() {
        OpenAPI openApi = openApiConfiguration.openApi();

        assertThat(openApi.getInfo().getTitle())
            .isEqualTo(EXPECTED_TITLE);
        assertThat(openApi.getInfo().getVersion())
            .isEqualTo(EXPECTED_VERSION);
    }

    @Test
    void given_configuration_when_openApi_then_serverUrlIsCorrect() {
        OpenAPI openApi = openApiConfiguration.openApi();

        assertThat(openApi.getServers()).hasSize(1);
        assertThat(openApi.getServers().getFirst().getUrl())
            .isEqualTo(EXPECTED_SERVER_URL);
    }

}

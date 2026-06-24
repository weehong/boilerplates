package com.example.boilerplate.shared.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private static final String API_TITLE = "SpringBootBoilerplate API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "SpringBootBoilerplate API";
    private static final String CONTACT_NAME = "SpringBootBoilerplate";
    private static final String LICENSE_NAME = "All Rights Reserved";
    private static final String SERVER_URL_PREFIX = "http://localhost:";
    private static final String SERVER_DESCRIPTION = "Local";

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                      .title(API_TITLE)
                      .version(API_VERSION)
                      .description(API_DESCRIPTION)
                      .contact(new Contact()
                                   .name(CONTACT_NAME))
                      .license(new License()
                                   .name(LICENSE_NAME)))
            .servers(List.of(
                new Server()
                    .url(SERVER_URL_PREFIX + serverPort)
                    .description(SERVER_DESCRIPTION)));
    }

}

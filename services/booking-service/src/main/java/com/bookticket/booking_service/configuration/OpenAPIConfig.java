package com.bookticket.booking_service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("booking-service")
                .packagesToScan("com.bookticket.booking_service")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Do not manually add servers. Let SpringDoc handle it.
                // .addServersItem(new Server().url(API_GATEWAY_URL))
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(SCHEME)
                                        .bearerFormat(BEARER_FORMAT)));
    }

    private Info apiInfo() {
        return new Info()
                .title("Booking Service API")
                .description("""
                        <h2>Booking Service API Documentation</h2>
                        <p>This service manages ticket bookings, show reservations, and seat selections.</p>
                        """)
                .version("1.0")
                .contact(new Contact()
                        .name("BookTicket Support")
                        .email("bookticket.com@gmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}

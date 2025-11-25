package com.bookticket.api_gateway.configuration;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This configuration programmatically overrides the server URLs for all downstream
 * microservices discovered by the API Gateway. It ensures that the Swagger UI
 * always shows the public API Gateway URL, regardless of what the individual
 * services report.
 */
@Configuration
public class OpenApiGatewayConfig {

    @Value("${api-gateway.public-url:https://api-gateway-mtiq.onrender.com}")
    private String gatewayPublicUrl;

    @Bean
    public OpenApiCustomizer gatewayOpenApiCustomizer() {
        return openApi -> {
            // Create a new server object with the public URL of the API Gateway
            Server gatewayServer = new Server()
                    .url(gatewayPublicUrl)
                    .description("BookTicket Production API");

            // Clear any existing servers (like http://10.x.x.x) and add only the gateway server
            openApi.setServers(List.of(gatewayServer));
        };
    }
}

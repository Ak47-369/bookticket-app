package com.bookticket.theater_service.configuration;

import com.bookticket.theater_service.security.HeaderPropagationInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public HeaderPropagationInterceptor headerPropagationInterceptor() {
        return new HeaderPropagationInterceptor();
    }

    @Bean("movieApiClient")
    @LoadBalanced
    public RestClient.Builder movieRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient movieRestClient(RestClient.Builder movieRestClientBuilder) {
        return movieRestClientBuilder
                .baseUrl("lb://movie-service")
                .requestInterceptor(headerPropagationInterceptor())
                .build();
    }
}

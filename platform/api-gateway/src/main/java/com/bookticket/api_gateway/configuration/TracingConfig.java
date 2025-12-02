package com.bookticket.api_gateway.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * This configuration adds a reactive GlobalFilter to include tracing information
 * (traceId and spanId) in the HTTP response headers for every request that passes
 * through the API Gateway.
 */
@Configuration
public class TracingConfig {

    private final Tracer tracer;

    public TracingConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean
    public GlobalFilter traceIdResponseFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    Span currentSpan = tracer.currentSpan();

                    if (currentSpan != null) {
                        // Add the traceId and spanId to the response headers
                        exchange.getResponse().getHeaders().add("X-Trace-ID", currentSpan.context().traceId());
                        exchange.getResponse().getHeaders().add("X-Span-ID", currentSpan.context().spanId());
                    }
                }));
    }
}

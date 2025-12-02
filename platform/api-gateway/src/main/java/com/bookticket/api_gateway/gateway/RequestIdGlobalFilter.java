package com.bookticket.api_gateway.gateway;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_SPAN_ID = "spanId";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Run before any other filter
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Get or generate request ID
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // Generate trace and span IDs if not present in request
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        String spanId = exchange.getRequest().getHeaders().getFirst(SPAN_ID_HEADER);
        if (spanId == null || spanId.isEmpty()) {
            // Generate a shorter ID for the span
            spanId = UUID.randomUUID().toString().substring(0, 8);
        }

        // Mutate the request to pass headers downstream
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .header(TRACE_ID_HEADER, traceId)
                .header(SPAN_ID_HEADER, spanId)
                .build();

        // Add to MDC for logging
        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_SPAN_ID, spanId);

        // Add to response headers
        exchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);
        exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);
        exchange.getResponse().getHeaders().set(SPAN_ID_HEADER, spanId);

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> MDC.clear()); // Cleanup MDC
    }
}

package com.bookticket.movie_service.security;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

public class HeaderPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            String userId = attributes.getRequest().getHeader("X-User-Id");
            String userRoles = attributes.getRequest().getHeader("X-User-Roles");
            String username = attributes.getRequest().getHeader("X-User-Name");
            userRoles += ",SERVICE_ACCOUNT"; // Service To Service communication
            
            if (userId != null) {
                request.getHeaders().set("X-User-Id", userId);
            }

            if (username != null) {
                request.getHeaders().set("X-User-Name", username);
            }
            
            if (userRoles != null) {
                request.getHeaders().set("X-User-Roles", userRoles);
            }
        }
        
        return execution.execute(request, body);
    }
}


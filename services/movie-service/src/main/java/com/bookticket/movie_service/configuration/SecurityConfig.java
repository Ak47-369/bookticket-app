package com.bookticket.movie_service.configuration;

import com.bookticket.movie_service.security.HeaderAuthenticatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public HeaderAuthenticatorFilter headerAuthenticatorFilter() {
        return new HeaderAuthenticatorFilter();
    }
    
    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/movies/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/movies/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/movies/**").hasAnyRole("USER", "ADMIN", "THEATER_OWNER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthenticatorFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

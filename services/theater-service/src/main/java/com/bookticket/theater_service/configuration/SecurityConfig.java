package com.bookticket.theater_service.configuration;


import com.bookticket.theater_service.security.HeaderAuthenticatorFilter;
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
                        // Internal service-to-service communication
                        .requestMatchers("/api/v1/shows/internal/**").hasRole("SERVICE_ACCOUNT")
                        // Screens (sub-resource of Theaters)
                        .requestMatchers(HttpMethod.POST, "/api/v1/theaters/*/screens").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/theaters/*/screens").permitAll() // Users should be able to see screens for a theater
                        // Theaters
                        .requestMatchers(HttpMethod.POST,"/api/v1/theaters/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/theaters/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/theaters/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/theaters/**").permitAll() // Users should be able to browse theaters
                        // Seats Sub Resource of Screen
                        .requestMatchers(HttpMethod.POST, "/api/v1/screens/*/seats/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/screens/*/seats/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/screens/*/seats/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/screens/*/seats/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/screens/*/seats/**").permitAll()
                        // Screens (direct resource access)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/screens/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/screens/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/screens/**").permitAll() // Users should be able to get screen details
                        // Shows
                        .requestMatchers(HttpMethod.POST,"/api/v1/shows/**").hasAnyRole("ADMIN","THEATER_OWNER")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/shows/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/shows/**").hasAnyRole("ADMIN", "THEATER_OWNER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/shows/**").permitAll()
                        // Actuator and any other authenticated requests
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(headerAuthenticatorFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

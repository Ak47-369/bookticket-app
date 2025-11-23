package com.bookticket.theater_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableMethodSecurity
@EnableDiscoveryClient
@EnableTransactionManagement
public class TheaterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheaterServiceApplication.class, args);
	}

}

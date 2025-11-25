package com.bookticket.notification_service.configuration;

import brevo.ApiClient;
import brevoApi.TransactionalEmailsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrevoConfig {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Bean
    public TransactionalEmailsApi brevoTransactionalEmailsApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setApiKey(brevoApiKey);
        return new TransactionalEmailsApi(apiClient);
    }
}

package com.bookticket.notification_service.configuration;

import brevo.ApiClient;
import brevoApi.TransactionalEmailsApi;
import brevo.auth.ApiKeyAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrevoConfig {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Bean
    public TransactionalEmailsApi brevoTransactionalEmailsApi() {
        ApiClient apiClient = brevo.Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) apiClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(brevoApiKey);
        return new TransactionalEmailsApi(apiClient);
    }
}

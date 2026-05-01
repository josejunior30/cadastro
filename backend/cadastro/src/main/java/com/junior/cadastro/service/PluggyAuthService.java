package com.junior.cadastro.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.exceptions.PluggyIntegrationException;

@Service
public class PluggyAuthService {

    private final RestClient restClient;

    @Value("${pluggy.client-id}")
    private String clientId;

    @Value("${pluggy.client-secret}")
    private String clientSecret;

    private String cachedApiKey;
    private Instant cachedApiKeyExpiresAt;

    public PluggyAuthService(
            RestClient.Builder builder,
            @Value("${pluggy.base-url}") String baseUrl
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public String getApiKey() {
        if (cachedApiKey != null
                && cachedApiKeyExpiresAt != null
                && Instant.now().isBefore(cachedApiKeyExpiresAt)) {
            return cachedApiKey;
        }

        Map<String, String> body = Map.of(
                "clientId", clientId,
                "clientSecret", clientSecret
        );

        JsonNode response = restClient.post()
                .uri("/auth")
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        String apiKey = response.path("apiKey").asText(null);

        if (apiKey == null || apiKey.isBlank()) {
            throw new PluggyIntegrationException("Não foi possível autenticar na Pluggy.");
        }

        cachedApiKey = apiKey;
        cachedApiKeyExpiresAt = Instant.now().plusSeconds(60 * 110);

        return cachedApiKey;
    }
}
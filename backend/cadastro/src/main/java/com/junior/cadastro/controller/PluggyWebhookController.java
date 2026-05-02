package com.junior.cadastro.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cadastro.DTO.PluggyWebhookEvent;
import com.junior.cadastro.exceptions.ApiError;
import com.junior.cadastro.service.PluggyWebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/webhooks/pluggy")
@Tag(name = "Webhooks Pluggy", description = "Recebimento de eventos enviados pela Pluggy")
public class PluggyWebhookController {

    private final PluggyWebhookService pluggyWebhookService;

    @Value("${pluggy.webhook-secret}")
    private String webhookSecret;

    public PluggyWebhookController(PluggyWebhookService pluggyWebhookService) {
        this.pluggyWebhookService = pluggyWebhookService;
    }

    @PostMapping
    @Operation(
        summary = "Recebe webhook da Pluggy",
        description = "Valida o segredo do webhook e processa eventos de item, conta e transações."
    )
    @ApiResponse(responseCode = "202", description = "Webhook aceito para processamento", content = @Content)
    @ApiResponse(responseCode = "400", description = "JSON inválido ou corpo malformado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Secret ausente ou inválido", content = @Content)
    @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String headerSecret,
            @RequestParam(value = "secret", required = false) String querySecret,
            @RequestBody PluggyWebhookEvent event
    ) {
        if (!isValidSecret(headerSecret, querySecret)) {
            return ResponseEntity.status(401).build();
        }

        pluggyWebhookService.handle(event);

        return ResponseEntity.accepted().build();
    }

    private boolean isValidSecret(String headerSecret, String querySecret) {
        String receivedSecret = headerSecret != null && !headerSecret.isBlank()
                ? headerSecret
                : querySecret;

        if (receivedSecret == null || receivedSecret.isBlank()) {
            return false;
        }

        byte[] expected = webhookSecret.getBytes(StandardCharsets.UTF_8);
        byte[] received = receivedSecret.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expected, received);
    }
}
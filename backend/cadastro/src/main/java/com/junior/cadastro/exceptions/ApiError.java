package com.junior.cadastro.exceptions;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Resposta padrão de erro da API")
public class ApiError {

    @Schema(example = "2026-05-02T12:00:00Z")
    private Instant timestamp;

    @Schema(example = "400")
    private Integer status;

    @Schema(example = "Erro de validação")
    private String error;

    @Schema(example = "email: email obrigatório")
    private String message;

    @Schema(example = "/auth/login")
    private String path;

    public ApiError() {
    }

    public ApiError(Instant timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
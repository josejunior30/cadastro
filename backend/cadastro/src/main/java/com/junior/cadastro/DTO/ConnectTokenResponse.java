package com.junior.cadastro.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com o token de conexão da Pluggy")
public class ConnectTokenResponse {
	@Schema(example = "pluggy-connect-token")
    private String accessToken;

    public ConnectTokenResponse() {
    }

    public ConnectTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
package com.junior.cadastro.DTO;

public class ConnectTokenResponse {

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
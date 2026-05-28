package com.example.moneyapp.data.remote.request;

public class LogoutRequest {
    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

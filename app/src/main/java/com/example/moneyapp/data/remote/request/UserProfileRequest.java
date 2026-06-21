package com.example.moneyapp.data.remote.request;

public class UserProfileRequest {
    private String name;
    private String email;
    private String defaultCurrency;
    private String imageUrl;
    private String phoneNumber;

    public UserProfileRequest(String name, String email, String defaultCurrency, String imageUrl, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.defaultCurrency = defaultCurrency;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
    }
}

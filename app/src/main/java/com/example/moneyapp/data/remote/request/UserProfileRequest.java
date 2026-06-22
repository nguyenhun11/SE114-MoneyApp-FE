package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class UserProfileRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("defaultCurrency")
    private String defaultCurrency;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("phoneNumber")
    private String phoneNumber;

    public UserProfileRequest(String name, String email, String imageUrl, String phoneNumber, String defaultCurrency) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.defaultCurrency = defaultCurrency;
    }
}

package com.example.moneyapp.data.remote.request;

public class UserProfileRequest {
    private String name;
    private String email;
    private String imageUrl;
    private String phoneNumber;

    public UserProfileRequest(String name, String email, String imageUrl, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

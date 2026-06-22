package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("dailyStreak")
    private int dailyStreak;
    @SerializedName("todayCheckedIn")
    private boolean todayCheckedIn;
    @SerializedName("defaultCurrency")
    private String defaultCurrency;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("lastUpdatedAt")
    private String lastUpdatedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public int getDailyStreak() { return dailyStreak; }
    public void setDailyStreak(int dailyStreak) { this.dailyStreak = dailyStreak; }
    public boolean isTodayCheckedIn() { return todayCheckedIn; }
    public void setTodayCheckedIn(boolean todayCheckedIn) { this.todayCheckedIn = todayCheckedIn; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(String lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}

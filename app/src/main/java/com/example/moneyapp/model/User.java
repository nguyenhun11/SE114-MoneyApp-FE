package com.example.moneyapp.model;

import java.util.Date;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private int dailyStreak;
    private boolean todayCheckedIn;
    private String defaultCurrency;
    private Date createdAt;
    private Date updatedAt;

    public User(int userId, String name, String email, String phoneNumber, String profileImageUrl, int dailyStreak, boolean todayCheckedIn, String defaultCurrency, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.dailyStreak = dailyStreak;
        this.todayCheckedIn = todayCheckedIn;
        this.defaultCurrency = defaultCurrency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getDailyStreak() {
        return dailyStreak;
    }

    public void setDailyStreak(int dailyStreak) {
        this.dailyStreak = dailyStreak;
    }

    public boolean isTodayCheckedIn() {
        return todayCheckedIn;
    }

    public void setTodayCheckedIn(boolean todayCheckedIn) {
        this.todayCheckedIn = todayCheckedIn;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}

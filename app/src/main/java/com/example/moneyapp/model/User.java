package com.example.moneyapp.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private int dailyStreak;
    private boolean todayCheckedIn;

    public User(String userId, String name, String email, String phoneNumber, String profileImageUrl, int dailyStreak, boolean todayCheckedIn) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.dailyStreak = dailyStreak;
        this.todayCheckedIn = todayCheckedIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
}

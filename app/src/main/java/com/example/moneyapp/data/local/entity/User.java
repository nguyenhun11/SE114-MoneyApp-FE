package com.example.moneyapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String email;
    private String profileImageUrl;
    private double balance;

    public User(@NonNull String id, String name, String email, String profileImageUrl, double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.balance = balance;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

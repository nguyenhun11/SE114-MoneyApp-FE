package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class GoalResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("targetAmount")
    private double targetAmount;

    @SerializedName("currentAmount")
    private double currentAmount;

    @SerializedName("deadline")
    private String deadline;

    @SerializedName("iconId")
    private int iconId;

    @SerializedName("colorId")
    private int colorId;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("progress")
    private Double progress; // Optional, returned by deposit endpoint

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public String getDeadline() { return deadline; }
    public int getIconId() { return iconId; }
    public int getColorId() { return colorId; }
    public boolean isActive() { return isActive; }
    public Double getProgress() { return progress; }
}

package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class GoalRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("targetAmount")
    private double targetAmount;

    @SerializedName("deadline")
    private String deadline; // ISO 8601 format

    @SerializedName("iconId")
    private int iconId;

    @SerializedName("colorId")
    private int colorId;

    public GoalRequest(String name, double targetAmount, String deadline, int iconId, int colorId) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.iconId = iconId;
        this.colorId = colorId;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getIconId() { return iconId; }
    public void setIconId(int iconId) { this.iconId = iconId; }

    public int getColorId() { return colorId; }
    public void setColorId(int colorId) { this.colorId = colorId; }
}

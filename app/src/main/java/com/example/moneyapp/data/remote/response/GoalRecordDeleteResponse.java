package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class GoalRecordDeleteResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("newGoalAmount")
    private double newGoalAmount;

    public String getMessage() {
        return message;
    }

    public double getNewGoalAmount() {
        return newGoalAmount;
    }
}

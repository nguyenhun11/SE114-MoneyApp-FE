package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class GoalTransactionResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("currentAmount")
    private double currentAmount;
    @SerializedName("progress")
    private double progress;
    @SerializedName("accountAvailableBalance")
    private double accountAvailableBalance;

    public String getMessage() {
        return message;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getProgress() {
        return progress;
    }

    public double getAccountAvailableBalance() {
        return accountAvailableBalance;
    }
}

package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class GoalRecordResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("goalId")
    private int goalId;

    @SerializedName("accountId")
    private String accountId; // Dùng String để hứng kiểu Guid từ C#

    @SerializedName("accountName")
    private String accountName;

    @SerializedName("amount")
    private double amount;

    @SerializedName("type")
    private String type; // "Deposit" hoặc "Withdraw"

    @SerializedName("createdAt")
    private String createdAt;

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getGoalId() {
        return goalId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}

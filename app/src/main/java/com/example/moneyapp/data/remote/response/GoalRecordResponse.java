package com.example.moneyapp.data.remote.response;

import com.example.moneyapp.utils.DateConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class GoalRecordResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("goalId")
    private int goalId;

    @SerializedName("goalName")
    private String goalName;

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

    public Date getCreatedAt() {
        return DateConverter.convertStringToDate(createdAt);
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

    public String getGoalName() {
        return goalName;
    }
}

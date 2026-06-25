package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class BudgetResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("categoryGroupId")
    private String categoryGroupId;
    @SerializedName("categoryName")
    private String categoryName;
    @SerializedName("amount")
    private double amount;
    @SerializedName("usedAmount")
    private double usedAmount;
    @SerializedName("remainingAmount")
    private double remainingAmount;
    @SerializedName("percentageUsed")
    private double percentageUsed;
    @SerializedName("period")
    private int period;
    @SerializedName("isActive")
    private boolean isActive;
    @SerializedName("cycleName")
    private String cycleName;

    // Getters
    public int getId() { return id; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public double getAmount() { return amount; }
    public double getUsedAmount() { return usedAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public double getPercentageUsed() { return percentageUsed; }
    public int getPeriod() { return period; }
    public boolean isActive() { return isActive; }

    public String getCategoryGroupId() {
        return categoryGroupId;
    }

    public String getCycleName() {
        return cycleName;
    }
}

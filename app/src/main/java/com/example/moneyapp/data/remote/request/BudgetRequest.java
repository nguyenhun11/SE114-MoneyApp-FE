package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class BudgetRequest {
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("categoryGroupId")
    private String categoryGroupId;
    @SerializedName("amount")
    private double amount;
    @SerializedName("period")
    private int period; // 0: Weekly, 1: Monthly, 2: Yearly

    public BudgetRequest(String categoryId, String categoryGroupId, double amount, int period) {
        this.categoryId = categoryId;
        this.categoryGroupId = categoryGroupId;
        this.amount = amount;
        this.period = period;
    }

    // Getters and Setters
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public String getCategoryGroupId() {
        return categoryGroupId;
    }

    public void setCategoryGroupId(String categoryGroupId) {
        this.categoryGroupId = categoryGroupId;
    }
}

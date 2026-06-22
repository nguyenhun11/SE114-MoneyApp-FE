package com.example.moneyapp.data.remote.response;

public class BudgetResponse {
    private int id;
    private String categoryId;
    private String categoryName;
    private double amount;
    private double usedAmount;
    private double remainingAmount;
    private double percentageUsed;
    private int period;
    private String startDate;
    private boolean isActive;

    // Getters
    public int getId() { return id; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public double getAmount() { return amount; }
    public double getUsedAmount() { return usedAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public double getPercentageUsed() { return percentageUsed; }
    public int getPeriod() { return period; }
    public String getStartDate() { return startDate; }
    public boolean isActive() { return isActive; }
}

package com.example.moneyapp.data.remote.request;

public class BudgetRequest {
    private String categoryId;
    private double amount;
    private int period; // 0: Weekly, 1: Monthly, 2: Yearly
    private String startDate;

    public BudgetRequest(String categoryId, double amount, int period, String startDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
    }

    // Getters and Setters
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
}

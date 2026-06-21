package com.example.moneyapp.data.remote.response;


public class AdjustBalanceResponse {
    private String id;
    private String accountId;
    private String accountName;
    private double amount;
    private String currencyCode;
    private String createdAt;

    public AdjustBalanceResponse(String id, String accountId, String accountName, double amount, String currencyCode, String createdAt) {
        this.id = id;
        this.accountId = accountId;


        this.accountName = accountName;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}

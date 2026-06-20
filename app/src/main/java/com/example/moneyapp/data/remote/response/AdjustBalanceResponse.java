package com.example.moneyapp.data.remote.response;


public class AdjustBalanceResponse {
    private String id;
    private String accountId;
    private String accountName;
    private double amount;
    private String createdAt;

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getAccountName() { return accountName; }
    public double getAmount() { return amount; }
    public String getCreatedAt() { return createdAt; }
}

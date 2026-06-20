package com.example.moneyapp.model;

import java.util.Date;

public class AdjustBalance {
    private  String id;
    private  String accountId;
    private  String accountName;
    private  double amount;
    private  Date createdAt;

    public AdjustBalance(String id, String accountId, String accountName, double amount, Date createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.accountName = accountName;
        this.amount = amount;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

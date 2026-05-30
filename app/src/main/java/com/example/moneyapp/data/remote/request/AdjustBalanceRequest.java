package com.example.moneyapp.data.remote.request;

public class AdjustBalanceRequest {
    private String accountId;
    private double amount;

    public AdjustBalanceRequest(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}

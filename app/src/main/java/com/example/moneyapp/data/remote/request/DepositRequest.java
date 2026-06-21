package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class DepositRequest {
    @SerializedName("amount")
    private double amount;

    public DepositRequest(double amount) {
        this.amount = amount;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

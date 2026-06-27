package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class WithdrawRequest {
    @SerializedName("amount")
    private double amount;
    @SerializedName("accountId")
    private String accountId;

    public WithdrawRequest(double amount, String accountId) {
        this.amount = amount;
        this.accountId = accountId;
    }
}

package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class TotalBalanceDto {
    @SerializedName("totalBalance")
    private double totalBalance;
    @SerializedName("lockedBalance")
    private double lockedBalance;
    @SerializedName("availableBalance")
    private double availableBalance;

    public double getTotalBalance() {
        return totalBalance;
    }

    public double getLockedBalance() {
        return lockedBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}

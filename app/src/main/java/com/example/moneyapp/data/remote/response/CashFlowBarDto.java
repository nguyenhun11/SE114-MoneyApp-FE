package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class CashFlowBarDto {
    @SerializedName("period")
    private String period;
    @SerializedName("totalIncome")
    private double totalIncome;
    @SerializedName("totalExpense")
    private double totalExpense;
    @SerializedName("totalSaved")
    private double totalSaved;
    @SerializedName("totalWithdrawn")
    private double totalWithdrawn;
    @SerializedName("netBalance")
    private double netBalance;

    public String getPeriod() {
        return period;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }

    public double getTotalSaved() {
        return totalSaved;
    }

    public double getTotalWithdrawn() {
        return totalWithdrawn;
    }
}

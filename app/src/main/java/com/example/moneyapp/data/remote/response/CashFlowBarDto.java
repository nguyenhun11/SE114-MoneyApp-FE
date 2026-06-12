package com.example.moneyapp.data.remote.response;

public class CashFlowBarDto {
    private String period;
    private double totalIncome;
    private double totalExpense;
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
}

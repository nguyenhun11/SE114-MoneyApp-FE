package com.example.moneyapp.data.remote.request;

public class AccountRequest {
    private String accountName;
    private double balance;
    private int colorId;
    private int iconId;
    private String description;
    private boolean includeInTotalBalance;

    public AccountRequest(String accountName, double balance, int colorId, int iconId, String description, boolean includeInTotalBalance) {
        this.accountName = accountName;
        this.balance = balance;
        this.colorId = colorId;
        this.iconId = iconId;
        this.description = description;
        this.includeInTotalBalance = includeInTotalBalance;
    }
}

package com.example.moneyapp.data.remote.response;

public class AccountResponse {
    private String id;
    private String accountName;
    private int colorId;
    private int iconId;
    private double balance;
    private String description;
    private boolean includeInTotalBalance;
    private int sortingOrder;
    private String createdAt;
    private String lastUpdatedAt;

    public AccountResponse(String id, String accountName, int colorId, int iconId, double balance, String description, boolean includeInTotalBalance, int sortingOrder, String createdAt, String lastUpdatedAt) {
        this.id = id;
        this.accountName = accountName;
        this.colorId = colorId;
        this.iconId = iconId;
        this.balance = balance;
        this.description = description;
        this.includeInTotalBalance = includeInTotalBalance;
        this.sortingOrder = sortingOrder;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIncludeInTotalBalance() {
        return includeInTotalBalance;
    }

    public void setIncludeInTotalBalance(boolean includeInTotalBalance) {
        this.includeInTotalBalance = includeInTotalBalance;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(int sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}

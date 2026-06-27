package com.example.moneyapp.model;

import java.util.Date;

public class Account {
    private String accountId;
    private String accountName;
    private double totalBalance;
    private double lockedBalance;
    private double availableBalance;
    private String currencyCode;
    private int icon;
    private int color;
    private String description;
    private Boolean includeInTotal;
    private int order;
    private Date createdAt;
    private Date updatedAt;

    public Account(String accountId,
                   String accountName,
                   Double totalBalance,
                   double lockedBalance,
                   double availableBalance,
                   String currencyCode,
                   int color,
                   int icon,
                   String description,
                   Boolean includeInTotal,
                   int order,
                   Date createdAt,
                   Date updatedAt) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.totalBalance = totalBalance;
        this.lockedBalance = lockedBalance;
        this.availableBalance = availableBalance;
        this.currencyCode = currencyCode;
        this.icon = icon;
        this.color = color;
        this.description = description;
        this.includeInTotal = includeInTotal;
        this.order = order;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isIncludeInTotal() {
        return includeInTotal;
    }

    public void setIncludeInTotal(Boolean includeInTotal) {
        this.includeInTotal = includeInTotal;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(double lockedBalance) {
        this.lockedBalance = lockedBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }
}

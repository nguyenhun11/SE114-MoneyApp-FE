package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class AccountResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("accountName")
    private String accountName;
    @SerializedName("colorId")
    private int colorId;
    @SerializedName("iconId")
    private int iconId;
    @SerializedName("totalBalance")
    private double totalBalance;
    @SerializedName("lockedBalance")
    private double lockedBalance;
    @SerializedName("availableBalance")
    private double availableBalance;
    @SerializedName("currencyCode")
    private String currencyCode;
    @SerializedName("description")
    private String description;
    @SerializedName("includeInTotalBalance")
    private boolean includeInTotalBalance;
    @SerializedName("sortingOrder")
    private int sortingOrder;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("lastUpdatedAt")
    private String lastUpdatedAt;

    public String getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getColorId() {
        return colorId;
    }

    public int getIconId() {
        return iconId;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public String getDescription() {
        return description;
    }
    public boolean isIncludeInTotalBalance() {
        return includeInTotalBalance;
    }
    public int getSortingOrder() {
        return sortingOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public double getLockedBalance() {
        return lockedBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}

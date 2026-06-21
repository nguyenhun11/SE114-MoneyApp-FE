package com.example.moneyapp.data.remote.response;

import java.util.List;

public class TransactionResponse {
    private String id;
    private String accountId;
    private String accountName;
    private String categoryId;
    private String categoryName;
    private Integer type;
    private Double originalAmount;
    private String currencyCode;
    private Double accountAmount;
    private Double baseAmount;
    private Double exchangeRate;
    private String date;
    private String note;
    private int categoryColorId;
    private int categoryIconId;
    private int accountColorId;
    private int accountIconId;
    private List<String> imageUrls;
    private int moodId;
    private String createdAt;
    private String lastUpdatedAt;

    public TransactionResponse(String id, String accountId, String accountName, String categoryId, String categoryName, Integer type, String currencyCode, Double accountAmount, Double baseAmount, Double originalAmount, Double exchangeRate, String date, String note, int categoryColorId, int categoryIconId, int accountColorId, int accountIconId, List<String> imageUrls, int moodId, String createdAt, String lastUpdatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.currencyCode = currencyCode;
        this.accountAmount = accountAmount;
        this.originalAmount = originalAmount;
        this.baseAmount = baseAmount;
        this.exchangeRate = exchangeRate;
        this.date = date;
        this.note = note;
        this.categoryColorId = categoryColorId;
        this.categoryIconId = categoryIconId;
        this.accountColorId = accountColorId;
        this.accountIconId = accountIconId;
        this.imageUrls = imageUrls;
        this.moodId = moodId;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getMoodId() {
        return moodId;
    }

    public void setMoodId(int moodId) {
        this.moodId = moodId;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getCategoryColorId() {
        return categoryColorId;
    }

    public void setCategoryColorId(int categoryColorId) {
        this.categoryColorId = categoryColorId;
    }

    public int getCategoryIconId() {
        return categoryIconId;
    }

    public void setCategoryIconId(int categoryIconId) {
        this.categoryIconId = categoryIconId;
    }

    public int getAccountColorId() {
        return accountColorId;
    }

    public void setAccountColorId(int accountColorId) {
        this.accountColorId = accountColorId;
    }

    public int getAccountIconId() {
        return accountIconId;
    }

    public void setAccountIconId(int accountIconId) {
        this.accountIconId = accountIconId;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(Double accountAmount) {
        this.accountAmount = accountAmount;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}

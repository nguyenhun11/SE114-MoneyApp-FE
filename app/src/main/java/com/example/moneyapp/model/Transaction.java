package com.example.moneyapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction {
    private String transactionId;
    private String accountId;
    private String accountName;
    private String categoryId;
    private String categoryName;
    private CategoryType type;
    private Double originalAmount;
    private String currencyCode;
    private Double accountAmount;
    private Double baseAmount;
    private Double exchangeRate;
    private Date date;
    private String note;
    private int categoryColorId;
    private int categoryIconId;
    private int accountColorId;
    private int accountIconId;
    private List<String> imageUrls;
    private int moodId;
    private Date createdAt;


    public Transaction(String transactionId,
                       String accountId,
                       String accountName,
                       String categoryId,
                       String categoryName,
                       CategoryType type, Double originalAmount, String currencyCode, Double accountAmount,
                       Double baseAmount, Double exchangeRate,
                       Date date,
                       String note, int categoryColorId, int categoryIconId, int accountColorId, int accountIconId,
                       List<String> imageUrls, int moodId, Date createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.originalAmount = originalAmount;
        this.currencyCode = currencyCode;
        this.accountAmount = accountAmount;
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
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public Double getBaseAmount() { return baseAmount; }
    public void setBaseAmount(Double baseAmount) { this.baseAmount = baseAmount; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    // Helpers cho View
    public String getFormattedAmount() {
        if (baseAmount == null) return "0";
        return String.format(Locale.getDefault(), "%,.0f", Math.abs(baseAmount)).replace(",", ".");
    }

    public String getFormattedDate() {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public String getFormattedTime() {
        if (date == null) return "";
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public String getSource() {
        return accountName != null ? accountName : accountId;
    }

    public String getTime() {
        return getFormattedTime();
    }
    
    public String getCategory() {
        return categoryName != null ? categoryName : categoryId;
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

    public int getMoodId() { return moodId; }
    public void setMoodId(int moodId) { this.moodId = moodId; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}

package com.example.moneyapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction {
    private String transactionId;
    private String accountId; // ID ví
    private String accountName;
    private String categoryId; // ID danh mục
    private String categoryName;
    private Double amount;
    private Date date;
    private String description;
    private List<String> imageUrls;

    public Transaction(String transactionId, String accountId, String categoryId, Double amount, Date date, String description, List<String> imageUrls) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.imageUrls = imageUrls;
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

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    // Helpers cho View
    public String getFormattedAmount() {
        if (amount == null) return "0";
        return String.format(Locale.getDefault(), "%,.0f", amount).replace(",", ".");
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
}

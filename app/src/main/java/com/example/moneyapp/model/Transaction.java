package com.example.moneyapp.model;

public class Transaction {
    private String category;
    private String description;
    private String amount;
    private String time;

    public Transaction(String category, String description, String amount, String time) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.time = time;
    }

    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getAmount() { return amount; }
    public String getTime() { return time; }
}
package com.example.moneyapp.model;

public class Transaction {
    private String category;
    private String description;
    private String amount;
    private String time;
    private String date;
    private String type;
    private String source;

    public Transaction(String category, String description, String amount,
                       String time, String date, String type, String source) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.time = time;
        this.date = date;
        this.type = type;
        this.source = source;
    }

    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getAmount() { return amount; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getSource() { return source; }

}
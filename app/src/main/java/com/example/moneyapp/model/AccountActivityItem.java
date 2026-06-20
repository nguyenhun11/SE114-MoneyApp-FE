package com.example.moneyapp.model;

import java.util.Date;

public class AccountActivityItem {
    public enum Type {
        TRANSFER,
        ADJUSTMENT
    }

    private String id;
    private Type type;
    private String sourceName;
    private String destinationName;
    private double amount;
    private Date date;
    private String description;

    public AccountActivityItem(String id, Type type, String sourceName, String destinationName, double amount, Date date, String description) {
        this.id = id;
        this.type = type;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getSourceName() { return sourceName; }
    public String getDestinationName() { return destinationName; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
}

package com.example.moneyapp.model;

import java.util.Date;

public class Transfer {
    private String id;
    private String sourceAccountId;
    private String sourceAccountName;
    private String destinationAccountId;
    private String destinationAccountName;
    private Double amount;
    private Date date;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Transfer(String id, String sourceAccountId, String sourceAccountName, String destinationAccountId, String destinationAccountName, Double amount, Date date, String description, Date createdAt, Date updatedAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountName = sourceAccountName;
        this.destinationAccountId = destinationAccountId;
        this.destinationAccountName = destinationAccountName;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getSourceAccountName() {
        return sourceAccountName;
    }

    public void setSourceAccountName(String sourceAccountName) {
        this.sourceAccountName = sourceAccountName;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(String destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}

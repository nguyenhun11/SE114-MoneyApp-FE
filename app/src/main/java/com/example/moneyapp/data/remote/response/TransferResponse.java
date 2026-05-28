package com.example.moneyapp.data.remote.response;

public class TransferResponse {
    private String id;
    private String sourceAccountId;
    private String sourceAccountName;
    private String destinationAccountId;
    private String destinationAccountName;
    private Double amount;
    private String transferDate;
    private String description;
    private String createdAt;
    private String lastUpdatedAt;

    public TransferResponse(String id,
                            String sourceAccountId,
                            String sourceAccountName,
                            String destinationAccountId,
                            String destinationAccountName,
                            Double amount, String transferDate,
                            String description,
                            String createdAt, 
                            String lastUpdatedAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountName = sourceAccountName;
        this.destinationAccountId = destinationAccountId;
        this.destinationAccountName = destinationAccountName;
        this.amount = amount;
        this.transferDate = transferDate;
        this.description = description;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
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

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

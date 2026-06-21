package com.example.moneyapp.data.remote.response;

public class TransferResponse {
    private String id;
    private String sourceAccountId;
    private String sourceAccountName;
    private int sourceAccountIconId;
    private int sourceAccountColorId;
    private String destinationAccountId;
    private String destinationAccountName;
    private int destinationAccountIconId;
    private int destinationAccountColorId;
    private Double sourceAmount;
    private Double destinationAmount;
    private Double baseAmount;
    private Double sourceExchangeRate;
    private Double destinationExchangeRate;
    private String transferDate;
    private String description;
    private String createdAt;
    private String lastUpdatedAt;

    public TransferResponse(String id,
                            String sourceAccountId,
                            String sourceAccountName, int sourceAccountIconId, int sourceAccountColorId,
                            String destinationAccountId,
                            String destinationAccountName, int destinationAccountIconId, int destinationAccountColorId,
                            Double sourceAmount, Double destinationAmount, Double baseAmount, Double sourceExchangeRate, Double destinationExchangeRate, String transferDate,
                            String description,
                            String createdAt,
                            String lastUpdatedAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountName = sourceAccountName;
        this.sourceAccountIconId = sourceAccountIconId;
        this.sourceAccountColorId = sourceAccountColorId;
        this.destinationAccountId = destinationAccountId;
        this.destinationAccountName = destinationAccountName;
        this.destinationAccountIconId = destinationAccountIconId;
        this.destinationAccountColorId = destinationAccountColorId;
        this.sourceAmount = sourceAmount;
        this.destinationAmount = destinationAmount;
        this.baseAmount = baseAmount;
        this.sourceExchangeRate = sourceExchangeRate;
        this.destinationExchangeRate = destinationExchangeRate;
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

    public Double getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(Double sourceAmount) {
        this.sourceAmount = sourceAmount;
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

    public int getSourceAccountIconId() {
        return sourceAccountIconId;
    }

    public void setSourceAccountIconId(int sourceAccountIconId) {
        this.sourceAccountIconId = sourceAccountIconId;
    }

    public int getSourceAccountColorId() {
        return sourceAccountColorId;
    }

    public void setSourceAccountColorId(int sourceAccountColorId) {
        this.sourceAccountColorId = sourceAccountColorId;
    }

    public int getDestinationAccountIconId() {
        return destinationAccountIconId;
    }

    public void setDestinationAccountIconId(int destinationAccountIconId) {
        this.destinationAccountIconId = destinationAccountIconId;
    }

    public int getDestinationAccountColorId() {
        return destinationAccountColorId;
    }

    public void setDestinationAccountColorId(int destinationAccountColorId) {
        this.destinationAccountColorId = destinationAccountColorId;
    }

    public Double getDestinationAmount() {
        return destinationAmount;
    }

    public void setDestinationAmount(Double destinationAmount) {
        this.destinationAmount = destinationAmount;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getSourceExchangeRate() {
        return sourceExchangeRate;
    }

    public void setSourceExchangeRate(Double sourceExchangeRate) {
        this.sourceExchangeRate = sourceExchangeRate;
    }

    public Double getDestinationExchangeRate() {
        return destinationExchangeRate;
    }

    public void setDestinationExchangeRate(Double destinationExchangeRate) {
        this.destinationExchangeRate = destinationExchangeRate;
    }
}

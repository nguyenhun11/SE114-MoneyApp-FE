package com.example.moneyapp.data.remote.request;

public class TransferRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private Double sourceAmount;
    private Double destinationAmount;
    private Double baseAmount;
    private Double sourceExchangeRate;
    private Double destinationExchangeRate;
    private String transferDate;
    private String description;

    public TransferRequest(String sourceAccountId, String destinationAccountId, Double sourceAmount, Double destinationAmount, Double baseAmount, Double sourceExchangeRate, Double destinationExchangeRate, String transferDate, String description) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.sourceAmount = sourceAmount;
        this.destinationAmount = destinationAmount;
        this.baseAmount = baseAmount;
        this.sourceExchangeRate = sourceExchangeRate;
        this.destinationExchangeRate = destinationExchangeRate;
        this.transferDate = transferDate;
        this.description = description;
    }
}

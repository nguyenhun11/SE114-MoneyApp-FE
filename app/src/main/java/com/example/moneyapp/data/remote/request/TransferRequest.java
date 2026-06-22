package com.example.moneyapp.data.remote.request;

public class TransferRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private Double sourceAmount;
    private String transferDate;
    private String description;

    public TransferRequest(String sourceAccountId, String destinationAccountId, Double sourceAmount, String transferDate, String description) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.sourceAmount = sourceAmount;
        this.transferDate = transferDate;
        this.description = description;
    }
}

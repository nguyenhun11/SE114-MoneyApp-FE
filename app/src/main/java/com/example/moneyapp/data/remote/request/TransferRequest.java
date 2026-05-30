package com.example.moneyapp.data.remote.request;

public class TransferRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private Double amount;
    private String transferDate;
    private String description;

    public TransferRequest(String sourceAccountId, String destinationAccountId, Double amount, String transferDate, String description) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.transferDate = transferDate;
        this.description = description;
    }
}

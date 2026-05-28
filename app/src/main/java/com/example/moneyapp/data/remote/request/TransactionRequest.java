package com.example.moneyapp.data.remote.request;

import java.util.List;

public class TransactionRequest {
    private String accountId;
    private String categoryId;
    private Double amount;
    private String date;
    private String note;
    private List<String> imageUrls;

    public TransactionRequest(String accountId, String categoryId, Double amount, String date, String note, List<String> imageUrls) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.imageUrls = imageUrls;
    }
}

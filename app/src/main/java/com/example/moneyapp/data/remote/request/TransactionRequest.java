package com.example.moneyapp.data.remote.request;

import java.util.List;

public class TransactionRequest {
    private String accountId;
    private String categoryId;
    private Double originalAmount;
    private String currencyCode;
    private String date;
    private String note;
    private List<String> imageUrls;
    private int moodId;

    public TransactionRequest(String accountId,
                              String categoryId,
                              Double originalAmount,
                              String currencyCode,
                              String date,
                              String note,
                              List<String> imageUrls,
                              int moodId) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.originalAmount = originalAmount;
        this.currencyCode = currencyCode;
        this.date = date;
        this.note = note;
        this.imageUrls = imageUrls;
        this.moodId = moodId;
    }
}

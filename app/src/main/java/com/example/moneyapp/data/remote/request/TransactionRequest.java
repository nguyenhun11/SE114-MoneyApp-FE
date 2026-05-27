package com.example.moneyapp.data.remote.request;

import java.util.List;

public class TransactionRequest {
    private String accountId;
    private String categoryId;
    private Double amount;
    private String date;
    private String note;
    private List<String> imageUrls;
}

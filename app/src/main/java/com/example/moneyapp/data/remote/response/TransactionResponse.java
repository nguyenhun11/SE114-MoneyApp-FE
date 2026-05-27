package com.example.moneyapp.data.remote.response;

import java.util.List;

public class TransactionResponse {
    private String id;
    private String accountId;
    private String categoryId;
    private Double amount;
    private String date;
    private String note;
    private List<String> imageUrls;
    private String createdAt;
    private String lastUpdatedAt;
}

package com.example.moneyapp.model;
import java.util.List;

public class DailyTransactionGroup {
    private String dateLabel;
    private String dateSummary;
    private List<Transaction> transactions;

    public DailyTransactionGroup(String dateLabel, String dateSummary, List<Transaction> transactions) {
        this.dateLabel = dateLabel;
        this.dateSummary = dateSummary;
        this.transactions = transactions;
    }

    public String getDateLabel() { return dateLabel; }
    public String getDateSummary() { return dateSummary; }
    public List<Transaction> getTransactions() { return transactions; }
}
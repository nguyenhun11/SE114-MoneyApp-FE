package com.example.moneyapp.model;

public class ListItem {
    public static final int TYPE_HEADER      = 0;
    public static final int TYPE_TRANSACTION = 1;

    private int type;
    private String dateLabel;
    private String dateSummary;
    private Transaction transaction;

    // Constructor header
    public ListItem(String dateLabel, String dateSummary) {
        this.type        = TYPE_HEADER;
        this.dateLabel   = dateLabel;
        this.dateSummary = dateSummary;
    }

    // Constructor transaction
    public ListItem(Transaction transaction) {
        this.type        = TYPE_TRANSACTION;
        this.transaction = transaction;
    }

    public int getType()                { return type; }
    public String getDateLabel()        { return dateLabel; }
    public String getDateSummary()      { return dateSummary; }
    public Transaction getTransaction() { return transaction; }
}
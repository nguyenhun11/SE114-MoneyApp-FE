package com.example.moneyapp.model;

public class Transaction {
    private String category;
    private String description;
    private String amount;
    private String time;
    private String date;
    private String type;
    private String source;

    public Transaction(String category, String description, String amount,
                       String time, String date, String type, String source) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.time = time;
        this.date = date;
        this.type = type;
        this.source = source;
    }

    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getAmount() { return amount; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getSource() { return source; }


    public class ListItem {
        public static final int TYPE_HEADER      = 0;
        public static final int TYPE_TRANSACTION = 1;

        private int type;

        // Dùng cho header
        private String dateLabel;
        private String dateSummary; // tổng tiền trong ngày

        // Dùng cho transaction
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

        public int getType()           { return type; }
        public String getDateLabel()   { return dateLabel; }
        public String getDateSummary() { return dateSummary; }
        public Transaction getTransaction() { return transaction; }
    }

}
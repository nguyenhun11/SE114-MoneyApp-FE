package com.example.moneyapp.model;

import java.util.Date;

public class HistoryItem {
    public static final int TYPE_TRANSFER = 1;
    public static final int TYPE_ADJUST_BALANCE = 2;

    private int type;
    private Date date; // Cần thiết để sort và gom nhóm

    private Transfer transfer;
    private AdjustBalance adjustBalance; // Model bạn tự map từ AdjustBalanceResponse

    // Constructor cho Transfer
    public HistoryItem(Transfer transfer) {
        this.type = TYPE_TRANSFER;
        this.transfer = transfer;
        this.date = transfer.getDate();
    }

    // Constructor cho AdjustBalance
    public HistoryItem(AdjustBalance adjustBalance) {
        this.type = TYPE_ADJUST_BALANCE;
        this.adjustBalance = adjustBalance;
        this.date = adjustBalance.getCreatedAt();
    }

    public int getType() { return type; }
    public Date getDate() { return date; }
    public Transfer getTransfer() { return transfer; }
    public AdjustBalance getAdjustBalance() { return adjustBalance; }
}
package com.example.moneyapp.model;

import java.util.Date;

public class HistoryItem {
    public static final int TYPE_TRANSFER = 0;
    public static final int TYPE_ADJUST_BALANCE = 1;

    private int type;
    private Transfer transfer;
    private AdjustBalance adjustBalance;

    public HistoryItem(Transfer transfer) {
        this.type = TYPE_TRANSFER;
        this.transfer = transfer;
    }

    public HistoryItem(AdjustBalance adjustBalance) {
        this.type = TYPE_ADJUST_BALANCE;
        this.adjustBalance = adjustBalance;
    }

    public int getType() {
        return type;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public AdjustBalance getAdjustBalance() {
        return adjustBalance;
    }
    public Date getDate() {
        if (type == TYPE_TRANSFER && transfer != null) {
            return transfer.getDate();
        } else if (type == TYPE_ADJUST_BALANCE && adjustBalance != null) {
            return adjustBalance.getCreatedAt();
        }
        return null;
    }
}
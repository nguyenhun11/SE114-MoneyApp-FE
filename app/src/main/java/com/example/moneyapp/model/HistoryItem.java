package com.example.moneyapp.model;

import java.util.Date;

public class HistoryItem {
    // Định nghĩa 3 loại giao dịch cho Adapter phân biệt
    public static final int TYPE_TRANSACTION = 0;
    public static final int TYPE_TRANSFER = 1;
    public static final int TYPE_ADJUST_BALANCE = 2;

    private int type;
    private Transaction transaction;
    private Transfer transfer;
    private AdjustBalance adjustBalance;

    // Khởi tạo cho Thu / Chi
    public HistoryItem(Transaction transaction) {
        this.type = TYPE_TRANSACTION;
        this.transaction = transaction;
    }

    // Khởi tạo cho Chuyển khoản
    public HistoryItem(Transfer transfer) {
        this.type = TYPE_TRANSFER;
        this.transfer = transfer;
    }

    // Khởi tạo cho Điều chỉnh số dư
    public HistoryItem(AdjustBalance adjustBalance) {
        this.type = TYPE_ADJUST_BALANCE;
        this.adjustBalance = adjustBalance;
    }

    public int getType() {
        return type;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public AdjustBalance getAdjustBalance() {
        return adjustBalance;
    }

    // Lấy ngày để ViewModel gom nhóm (Group)
    public Date getDate() {
        if (type == TYPE_TRANSACTION && transaction != null) {
            return transaction.getDate();
        } else if (type == TYPE_TRANSFER && transfer != null) {
            return transfer.getDate();
        } else if (type == TYPE_ADJUST_BALANCE && adjustBalance != null) {
            return adjustBalance.getCreatedAt();
        }
        return null;
    }
}
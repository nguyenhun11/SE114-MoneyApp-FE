package com.example.moneyapp.model;

import com.example.moneyapp.data.remote.response.GoalRecordResponse;
import com.example.moneyapp.utils.DateConverter;

import java.util.Date;

public class HistoryItem {
    // Định nghĩa 3 loại giao dịch cho Adapter phân biệt
    public static final int TYPE_TRANSACTION = 0;
    public static final int TYPE_TRANSFER = 1;
    public static final int TYPE_ADJUST_BALANCE = 2;
    public static final int TYPE_GOAL_RECORD = 3;

    private int type;
    private Transaction transaction;
    private Transfer transfer;
    private AdjustBalance adjustBalance;
    private GoalRecordResponse goalRecord;

    public HistoryItem(Transaction transaction) {
        this.type = TYPE_TRANSACTION;
        this.transaction = transaction;
    }

    public HistoryItem(Transfer transfer) {
        this.type = TYPE_TRANSFER;
        this.transfer = transfer;
    }

    public HistoryItem(AdjustBalance adjustBalance) {
        this.type = TYPE_ADJUST_BALANCE;
        this.adjustBalance = adjustBalance;
    }
    public HistoryItem(GoalRecordResponse goalRecord) {
        this.type = TYPE_GOAL_RECORD;
        this.goalRecord = goalRecord;
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
    public GoalRecordResponse getGoalRecord() { return goalRecord; }

    public Date getDate() {
        if (type == TYPE_TRANSACTION && transaction != null) {
            return transaction.getDate();
        } else if (type == TYPE_TRANSFER && transfer != null) {
            return transfer.getDate();
        } else if (type == TYPE_ADJUST_BALANCE && adjustBalance != null) {
            return adjustBalance.getCreatedAt();
        } else if (type == TYPE_GOAL_RECORD && goalRecord != null) {
            return DateConverter.convertStringToDate(goalRecord.getCreatedAt());
        }
        return null;
    }
}
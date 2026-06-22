package com.example.moneyapp.model;

import java.util.List;

public class DailyTransactionGroup {
    private String dateLabel;
    private String dateSummary;
    private List<HistoryItem> items; // ĐÃ SỬA: Dùng HistoryItem thay vì Transaction

    public DailyTransactionGroup(String dateLabel, String dateSummary, List<HistoryItem> items) {
        this.dateLabel = dateLabel;
        this.dateSummary = dateSummary;
        this.items = items;
    }

    public String getDateLabel() { return dateLabel; }
    public String getDateSummary() { return dateSummary; }

    // Đã đổi tên hàm và kiểu trả về
    public List<HistoryItem> getItems() { return items; }
}
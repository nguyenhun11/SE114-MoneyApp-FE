package com.example.moneyapp.model;

import java.util.List;

public class DailyTransferGroup {
    private String dateLabel;
    private List<HistoryItem> items;

    public DailyTransferGroup(String dateLabel, List<HistoryItem> items) {
        this.dateLabel = dateLabel;
        this.items = items;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public List<HistoryItem> getTransfers() {
        return items;
    }

    public void setTransfers(List<HistoryItem> items) {
        this.items = items;
    }
}

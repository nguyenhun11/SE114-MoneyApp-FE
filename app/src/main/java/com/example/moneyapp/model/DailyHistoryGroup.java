package com.example.moneyapp.model;

import java.util.List;

public class DailyHistoryGroup {
    private String dateLabel;
    private String dateSummary;
    private List<HistoryItem> items;

    public DailyHistoryGroup(String dateLabel, String dateSummary, List<HistoryItem> items) {
        this.dateLabel = dateLabel;
        this.dateSummary = dateSummary;
        this.items = items;
    }

    public String getDateLabel() { return dateLabel; }
    public String getDateSummary() { return dateSummary; }

    public List<HistoryItem> getItems() { return items; }
}
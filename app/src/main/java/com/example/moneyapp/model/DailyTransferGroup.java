package com.example.moneyapp.model;

import java.util.List;

public class DailyTransferGroup {
    private String dateLabel;
    private List<Transfer> transfers;

    public DailyTransferGroup(String dateLabel, List<Transfer> transfers) {
        this.dateLabel = dateLabel;
        this.transfers = transfers;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }
}

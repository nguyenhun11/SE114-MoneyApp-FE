package com.example.moneyapp.model;

import java.util.List;

public class AccountActivityGroup {
    private String dateLabel;
    private List<AccountActivityItem> activities;

    public AccountActivityGroup(String dateLabel, List<AccountActivityItem> activities) {
        this.dateLabel = dateLabel;
        this.activities = activities;
    }

    public String getDateLabel() { return dateLabel; }
    public List<AccountActivityItem> getActivities() { return activities; }
}

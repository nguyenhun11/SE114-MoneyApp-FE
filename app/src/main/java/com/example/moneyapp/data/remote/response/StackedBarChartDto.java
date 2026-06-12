package com.example.moneyapp.data.remote.response;

import java.util.List;

public class StackedBarChartDto {
    private String period;
    private List<CategoryPieChartDto> categoryBreakdowns;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<CategoryPieChartDto> getCategoryBreakdowns() {
        return categoryBreakdowns;
    }

    public void setCategoryBreakdowns(List<CategoryPieChartDto> categoryBreakdowns) {
        this.categoryBreakdowns = categoryBreakdowns;
    }
}

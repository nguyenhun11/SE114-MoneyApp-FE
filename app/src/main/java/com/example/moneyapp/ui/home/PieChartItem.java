package com.example.moneyapp.ui.home;

public class PieChartItem {
    private String categoryId;
    private String name;
    private Double amount;
    private float percentage;
    private int color;

    public PieChartItem(String categoryId, String name, Double amount, float percentage, int color) {
        this.categoryId = categoryId;
        this.name = name;
        this.amount = amount;
        this.percentage = percentage;
        this.color = color;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

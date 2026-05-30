package com.example.moneyapp.data.remote.response;

public class CategoryPieChartDto {
    private String categoryId;
    private String categoryName;
    private int colorId;
    private double totalAmount;
    private double percentage;

    public CategoryPieChartDto(String categoryId, String categoryName, int colorId, double totalAmount, double percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.colorId = colorId;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}

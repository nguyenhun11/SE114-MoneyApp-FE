package com.example.moneyapp.data.remote.response;

public class CategoryResponse {
    private String id;
    private String categoryName;
    private int type;
    private double monthlyTarget;
    private int colorId;
    private int iconId;
    private int sortingOrder;
    private String createdAt;
    private String lastUpdatedAt;

    public CategoryResponse(String id, String categoryName, int type, double monthlyTarget, int colorId, int iconId, boolean isDefault, int sortingOrder, String createdAt, String lastUpdatedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.type = type;
        this.monthlyTarget = monthlyTarget;
        this.colorId = colorId;
        this.iconId = iconId;
        this.sortingOrder = sortingOrder;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMonthlyTarget() {
        return monthlyTarget;
    }

    public void setMonthlyTarget(double monthlyTarget) {
        this.monthlyTarget = monthlyTarget;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(int sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}

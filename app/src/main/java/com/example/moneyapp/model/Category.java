package com.example.moneyapp.model;

import java.util.Date;

public class Category {
    private String categoryId;
    private String categoryName;
    private CategoryType type;
    private Double monthlyTarget;
    private int color;
    private int icon;
    private Boolean canEdit;
    private int order;
    private Date createdAt;
    private Date updatedAt;

    public Category(String categoryId, String categoryName, CategoryType type, Double monthlyTarget, int color, int icon, Boolean canEdit, int order, Date createdAt, Date updatedAt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.monthlyTarget = monthlyTarget;
        this.color = color;
        this.icon = icon;
        this.canEdit = canEdit;
        this.order = order;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Double getMonthlyTarget() {
        return monthlyTarget;
    }

    public void setMonthlyTarget(Double monthlyTarget) {
        this.monthlyTarget = monthlyTarget;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

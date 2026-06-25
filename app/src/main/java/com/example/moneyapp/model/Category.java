package com.example.moneyapp.model;

import com.example.moneyapp.data.remote.response.BudgetResponse;

import java.util.Date;
import java.util.List;

public class Category {
    private String categoryId;
    private String categoryName;
    private CategoryType type;
    private String groupId;
    private String groupName;
    private int color;
    private int icon;
    private int order;
    private Date createdAt;
    private Date updatedAt;
    private List<BudgetResponse> activeBudgets;

    public Category(String categoryId, String categoryName, CategoryType type, String groupId, String groupName, int color, int icon, int order, Date createdAt, Date updatedAt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.groupId = groupId;
        this.groupName = groupName;
        this.color = color;
        this.icon = icon;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<BudgetResponse> getActiveBudgets() {
        return activeBudgets;
    }
    public void setActiveBudgets(List<BudgetResponse> activeBudgets) {
        this.activeBudgets = activeBudgets;
    }
}

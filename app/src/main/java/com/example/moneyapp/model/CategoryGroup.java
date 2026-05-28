package com.example.moneyapp.model;

import java.util.Date;

public class CategoryGroup {
    private String groupId;
    private String groupName;
    private CategoryType type;
    private int sortingOrder;
    private Date createdAt;
    private Date updateAt;

    public CategoryGroup(String groupId, String groupName, CategoryType type, int sortingOrder, Date createdAt, Date updateAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.type = type;
        this.sortingOrder = sortingOrder;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(int sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}

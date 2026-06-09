package com.example.moneyapp.data.remote.request;

import com.example.moneyapp.model.CategoryType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CategoryGroupResponse {
    @SerializedName("id")
    private String groupId;
    
    @SerializedName("groupName")
    private String groupName;
    
    @SerializedName("type")
    private CategoryType type;
    
    @SerializedName("sortingOrder")
    private int sortingOrder;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("lastUpdatedAt")
    private Date updateAt;

    public CategoryGroupResponse(String groupId, String groupName, CategoryType type, int sortingOrder, Date createdAt, Date updateAt) {
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

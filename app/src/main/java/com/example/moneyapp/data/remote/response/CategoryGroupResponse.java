package com.example.moneyapp.data.remote.response;

import com.example.moneyapp.model.CategoryType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CategoryGroupResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("type")
    private CategoryType type;

    @SerializedName("sortingOrder")
    private int sortingOrder;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("lastUpdatedAt")
    private Date lastUpdatedAt;

    public CategoryGroupResponse(String id, String groupName, CategoryType type, int sortingOrder, Date createdAt, Date lastUpdatedAt) {
        this.id = id;
        this.groupName = groupName;
        this.type = type;
        this.sortingOrder = sortingOrder;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public CategoryType getType() {
        return type;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}

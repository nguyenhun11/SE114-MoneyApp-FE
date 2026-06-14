package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class CategoryResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("type")
    private int type;

    @SerializedName("categoryGroupId")
    private String categoryGroupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("monthlyTarget")
    private double monthlyTarget;

    @SerializedName("colorId")
    private int colorId;

    @SerializedName("iconId")
    private int iconId;

    @SerializedName("sortingOrder")
    private int sortingOrder;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("lastUpdatedAt")
    private String lastUpdatedAt;

    public CategoryResponse(String id, String categoryName, int type, String categoryGroupId, String groupName, double monthlyTarget, int colorId, int iconId, int sortingOrder, String createdAt, String lastUpdatedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.type = type;
        this.categoryGroupId = categoryGroupId;
        this.groupName = groupName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public int getType() {
        return type;
    }

    public double getMonthlyTarget() {
        return monthlyTarget;
    }

    public int getColorId() {
        return colorId;
    }

    public int getIconId() {
        return iconId;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public String getCategoryGroupId() {
        return categoryGroupId;
    }

    public String getGroupName() {
        return groupName;
    }
}

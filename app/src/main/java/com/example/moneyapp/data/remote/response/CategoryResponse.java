package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    @SerializedName("activeBudgets")
    private List<BudgetResponse> activeBudgets;

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getType() {
        return type;
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

    public List<BudgetResponse> getActiveBudgets() {
        return activeBudgets;
    }
}

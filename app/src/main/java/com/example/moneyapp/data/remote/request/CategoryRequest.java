package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class CategoryRequest {
    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("categoryGroupId")
    private String categoryGroupId;

    @SerializedName("monthlyTarget")
    private double monthlyTarget;

    @SerializedName("colorId")
    private int colorId;

    @SerializedName("iconId")
    private int iconId;

    public CategoryRequest(String categoryName, String categoryGroupId, double monthlyTarget, int colorId, int iconId) {
        this.categoryName = categoryName;
        this.categoryGroupId = categoryGroupId;
        this.monthlyTarget = monthlyTarget;
        this.colorId = colorId;
        this.iconId = iconId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryGroupId() {
        return categoryGroupId;
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
}

package com.example.moneyapp.data.remote.request;

public class CategoryRequest {
    private String categoryName;
    private double monthlyTarget;
    private int colorId;
    private int iconId;

    public CategoryRequest(String categoryName, double monthlyTarget, int colorId, int iconId) {
        this.categoryName = categoryName;
        this.monthlyTarget = monthlyTarget;
        this.colorId = colorId;
        this.iconId = iconId;
    }
}

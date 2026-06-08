package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class CategoryRequest {
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("monthlyTarget")
    private double monthlyTarget;
    
    @SerializedName("colorId")
    private int colorId;
    
    @SerializedName("iconId")
    private int iconId;
    
    @SerializedName("groupId")
    private String groupId;

    public CategoryRequest(String categoryName, double monthlyTarget, int colorId, int iconId, String groupId) {
        this.categoryName = categoryName;
        this.monthlyTarget = monthlyTarget;
        this.colorId = colorId;
        this.iconId = iconId;
        this.groupId = groupId;
    }
}

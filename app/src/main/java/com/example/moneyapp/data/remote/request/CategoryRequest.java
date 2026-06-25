package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class CategoryRequest {
    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("categoryGroupId")
    private String categoryGroupId;
    @SerializedName("colorId")
    private int colorId;
    @SerializedName("iconId")
    private int iconId;
    @SerializedName("budgetSetup")
    private BudgetRequest budgetSetup;

    public CategoryRequest(String categoryName, String categoryGroupId, int colorId, int iconId, BudgetRequest budgetSetup) {
        this.categoryName = categoryName;
        this.categoryGroupId = categoryGroupId;
        this.colorId = colorId;
        this.iconId = iconId;
        this.budgetSetup = budgetSetup;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryGroupId() {
        return categoryGroupId;
    }

    public int getColorId() {
        return colorId;
    }

    public int getIconId() {
        return iconId;
    }

    public BudgetRequest getBudgetSetup() {
        return budgetSetup;
    }

    public void setBudgetSetup(BudgetRequest budgetSetup) {
        this.budgetSetup = budgetSetup;
    }
}

package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class ReorderCategoryRequest {
    @SerializedName("newOrder")
    private int newOrder;

    public ReorderCategoryRequest(int newOrder) {
        this.newOrder = newOrder;
    }
}

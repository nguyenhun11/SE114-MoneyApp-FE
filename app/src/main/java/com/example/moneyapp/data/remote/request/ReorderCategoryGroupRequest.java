package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class ReorderCategoryGroupRequest {
    @SerializedName("newOrder")
    private int newOrder;

    public ReorderCategoryGroupRequest(int newOrder) {
        this.newOrder = newOrder;
    }
}

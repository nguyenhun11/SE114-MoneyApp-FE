package com.example.moneyapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class CategoryGroupRequest {
    @SerializedName("groupName")
    private String groupName;

    public CategoryGroupRequest(String groupName) {
        this.groupName = groupName;
    }
}

package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class RankItemDto {
    @SerializedName("rank")
    private int rank;

    @SerializedName("userId")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("prosperityPoints")
    private int prosperityPoints;

    @SerializedName("stabilityPoints")
    private int stabilityPoints;

    @SerializedName("cityLevel")
    private int cityLevel;

    // Getters
    public int getRank() { return rank; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public int getProsperityPoints() { return prosperityPoints; }
    public int getStabilityPoints() { return stabilityPoints; }
    public int getCityLevel() { return cityLevel; }
}

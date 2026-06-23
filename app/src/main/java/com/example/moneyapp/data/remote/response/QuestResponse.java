package com.example.moneyapp.data.remote.response;

public class QuestResponse {
    private String id;
    private String title;
    private String description;
    private int target;
    private int currentProgress;
    private int rewardPoints;
    private int rewardType; // 0: SP, 1: PP
    private boolean isCompleted;
    private boolean isClaimed;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTarget() { return target; }
    public int getCurrentProgress() { return currentProgress; }
    public int getRewardPoints() { return rewardPoints; }
    public int getRewardType() { return rewardType; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isClaimed() { return isClaimed; }
}

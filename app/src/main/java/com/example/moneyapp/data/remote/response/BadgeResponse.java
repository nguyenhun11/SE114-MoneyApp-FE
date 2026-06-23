package com.example.moneyapp.data.remote.response;

public class BadgeResponse {
    private String badgeId;
    private String name;
    private String description;
    private String iconKey;
    private boolean isUnlocked;
    private String unlockedAt;

    // Getters
    public String getBadgeId() { return badgeId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIconKey() { return iconKey; }
    public boolean isUnlocked() { return isUnlocked; }
    public String getUnlockedAt() { return unlockedAt; }
}

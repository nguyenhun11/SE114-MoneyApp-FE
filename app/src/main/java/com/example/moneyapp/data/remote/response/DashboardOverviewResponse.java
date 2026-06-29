package com.example.moneyapp.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardOverviewResponse {

    @SerializedName("userSummary")
    private UserSummary userSummary;

    @SerializedName("citySummary")
    private CitySummary citySummary;

    @SerializedName("smartInsights")
    private List<SmartInsight> smartInsights;

    @SerializedName("budgetAlerts")
    private List<BudgetAlert> budgetAlerts;

    @SerializedName("goalHighlights")
    private List<GoalHighlight> goalHighlights;

    @SerializedName("recentTransactions")
    private List<RecentTransaction> recentTransactions;

    @SerializedName("pendingQuests")
    private List<PendingQuest> pendingQuests;

    // --- GETTERS ---
    public UserSummary getUserSummary() { return userSummary; }
    public CitySummary getCitySummary() { return citySummary; }
    public List<SmartInsight> getSmartInsights() { return smartInsights; }
    public List<BudgetAlert> getBudgetAlerts() { return budgetAlerts; }
    public List<GoalHighlight> getGoalHighlights() { return goalHighlights; }
    public List<RecentTransaction> getRecentTransactions() { return recentTransactions; }
    public List<PendingQuest> getPendingQuests() { return pendingQuests; }

    // ==========================================
    // CÁC LỚP DATA CON (NESTED CLASSES)
    // ==========================================

    public static class UserSummary {
        @SerializedName("name")
        private String name;
        @SerializedName("imageUrl")
        private String imageUrl;
        @SerializedName("dailyStreak")
        private int dailyStreak;

        @SerializedName("todayCheckedIn")
        private boolean todayCheckedIn;

        public String getName() { return name; }
        public int getDailyStreak() { return dailyStreak; }
        public boolean isTodayCheckedIn() { return todayCheckedIn; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class CitySummary {
        @SerializedName("level")
        private int level;

        @SerializedName("prosperityPoints")
        private int prosperityPoints;

        @SerializedName("stabilityPoints")
        private int stabilityPoints;

        public int getLevel() { return level; }
        public int getProsperityPoints() { return prosperityPoints; }
        public int getStabilityPoints() { return stabilityPoints; }
    }

    public static class SmartInsight {
        @SerializedName("type")
        private String type; // INFO, SUCCESS, DANGER

        @SerializedName("title")
        private String title;

        @SerializedName("message")
        private String message;

        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
    }

    public static class BudgetAlert {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("amount")
        private double amount;

        @SerializedName("usedAmount")
        private double usedAmount;

        @SerializedName("percent")
        private int percent;

        @SerializedName("status")
        private String status; // WARNING, OVER

        public int getId() { return id; }
        public String getName() { return name; }
        public double getAmount() { return amount; }
        public double getUsedAmount() { return usedAmount; }
        public int getPercent() { return percent; }
        public String getStatus() { return status; }
    }

    public static class GoalHighlight {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("targetAmount")
        private double targetAmount;

        @SerializedName("currentAmount")
        private double currentAmount;

        @SerializedName("progressPercent")
        private int progressPercent;

        @SerializedName("iconId")
        private int iconId;

        @SerializedName("colorId")
        private int colorId;

        public int getId() { return id; }
        public String getName() { return name; }
        public double getTargetAmount() { return targetAmount; }
        public double getCurrentAmount() { return currentAmount; }
        public int getProgressPercent() { return progressPercent; }
        public int getIconId() { return iconId; }
        public int getColorId() { return colorId; }
    }

    public static class RecentTransaction {
        @SerializedName("id")
        private String id; // Guid bên C# map sang String bên Java

        @SerializedName("categoryName")
        private String categoryName;

        @SerializedName("amount")
        private double amount;

        @SerializedName("type")
        private String type; // EXPENSE, INCOME...

        @SerializedName("iconId")
        private int iconId;

        @SerializedName("colorId")
        private int colorId;

        @SerializedName("date")
        private String date; // Nhận chuỗi ISO 8601 từ server

        public String getId() { return id; }
        public String getCategoryName() { return categoryName; }
        public double getAmount() { return amount; }
        public String getType() { return type; }
        public int getIconId() { return iconId; }
        public int getColorId() { return colorId; }
        public String getDate() { return date; }
    }

    public static class PendingQuest {
        @SerializedName("id")
        private String id; // Đã chốt QuestId là String!

        @SerializedName("title")
        private String title;

        @SerializedName("currentProgress")
        private int currentProgress;

        @SerializedName("target")
        private int target;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public int getCurrentProgress() { return currentProgress; }
        public int getTarget() { return target; }
    }
}
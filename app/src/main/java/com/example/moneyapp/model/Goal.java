package com.example.moneyapp.model;

import java.io.Serializable;

public class Goal implements Serializable {
    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private String deadline;
    private int iconId;
    private int colorId;
    private boolean isActive;

    public Goal(int id, String name, double targetAmount, double currentAmount, String deadline, int iconId, int colorId, boolean isActive) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.iconId = iconId;
        this.colorId = colorId;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getIconId() { return iconId; }
    public void setIconId(int iconId) { this.iconId = iconId; }

    public int getColorId() { return colorId; }
    public void setColorId(int colorId) { this.colorId = colorId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getProgressPercent() {
        if (targetAmount <= 0) return 0;
        int percent = (int) ((currentAmount / targetAmount) * 100);
        return Math.min(percent, 100);
    }
}

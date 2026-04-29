package com.example.moneyapp.model;

public class CategoryExpense {
    private String name;
    private long amount;
    private float percentage;
    private int color;

    public CategoryExpense(String name, long amount, float percentage, int color) {
        this.name = name;
        this.amount = amount;
        this.percentage = percentage;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

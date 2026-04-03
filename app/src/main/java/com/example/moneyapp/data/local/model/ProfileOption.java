package com.example.moneyapp.data.local.model;

public class ProfileOption {
    private int id;
    private int iconRes;
    private String title;

    public ProfileOption(int id, int iconRes, String title) {
        this.id = id;
        this.iconRes = iconRes;
        this.title = title;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

package com.example.moneyapp.view.city;

public class BuildOption {
    private String type;
    private String name;
    private String description;
    private int cost;

    public BuildOption(String type, String name, String description, int cost) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCost() { return cost; }
}

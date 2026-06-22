package com.example.moneyapp.data.remote.request;

public class BuildRequest {
    private String buildingType;
    private int positionX;
    private int positionY;

    public BuildRequest(String buildingType, int positionX, int positionY) {
        this.buildingType = buildingType;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    // Getters and Setters
    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }
    public int getPositionX() { return positionX; }
    public void setPositionX(int positionX) { this.positionX = positionX; }
    public int getPositionY() { return positionY; }
    public void setPositionY(int positionY) { this.positionY = positionY; }
}

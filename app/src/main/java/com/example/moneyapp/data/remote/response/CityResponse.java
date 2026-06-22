package com.example.moneyapp.data.remote.response;

import java.util.List;

public class CityResponse {
    private int level;
    private int prosperityPoints;
    private int stabilityPoints;
    private int currentStreak;
    private List<BuildingDto> buildings;

    public static class BuildingDto {
        private int id;
        private String buildingType;
        private int positionX;
        private int positionY;
        private int level;

        // Getters
        public int getId() { return id; }
        public String getBuildingType() { return buildingType; }
        public int getPositionX() { return positionX; }
        public int getPositionY() { return positionY; }
        public int getLevel() { return level; }
    }

    // Getters
    public int getLevel() { return level; }
    public int getProsperityPoints() { return prosperityPoints; }
    public int getStabilityPoints() { return stabilityPoints; }
    public int getCurrentStreak() { return currentStreak; }
    public List<BuildingDto> getBuildings() { return buildings; }
}

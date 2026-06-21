package com.example.moneyapp.data.remote.response;

public class CheckInResponse {
    private String message;
    private int currentStreak;
    private boolean isIncreased;

    public CheckInResponse(String message, int currentStreak, boolean isIncreased) {
        this.message = message;
        this.currentStreak = currentStreak;
        this.isIncreased = isIncreased;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public boolean isIncreased() {
        return isIncreased;
    }

    public void setIncreased(boolean increased) {
        isIncreased = increased;
    }
}

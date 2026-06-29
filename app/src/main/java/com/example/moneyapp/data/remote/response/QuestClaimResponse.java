package com.example.moneyapp.data.remote.response;

public class QuestClaimResponse {
    private String message;
    private int basePP;
    private int bonusPP;
    private int totalPP;
    private int baseSP;
    private int bonusSP;
    private int totalSP;

    public QuestClaimResponse(String message, int basePP, int bonusPP, int totalPP, int baseSP, int bonusSP, int totalSP) {
        this.message = message;
        this.basePP = basePP;
        this.bonusPP = bonusPP;
        this.totalPP = totalPP;
        this.baseSP = baseSP;
        this.bonusSP = bonusSP;
        this.totalSP = totalSP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getBasePP() {
        return basePP;
    }

    public void setBasePP(int basePP) {
        this.basePP = basePP;
    }

    public int getBonusPP() {
        return bonusPP;
    }

    public void setBonusPP(int bonusPP) {
        this.bonusPP = bonusPP;
    }

    public int getTotalPP() {
        return totalPP;
    }

    public void setTotalPP(int totalPP) {
        this.totalPP = totalPP;
    }

    public int getBaseSP() {
        return baseSP;
    }

    public void setBaseSP(int baseSP) {
        this.baseSP = baseSP;
    }

    public int getBonusSP() {
        return bonusSP;
    }

    public void setBonusSP(int bonusSP) {
        this.bonusSP = bonusSP;
    }

    public int getTotalSP() {
        return totalSP;
    }

    public void setTotalSP(int totalSP) {
        this.totalSP = totalSP;
    }
}

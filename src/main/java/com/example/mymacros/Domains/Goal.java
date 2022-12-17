package com.example.mymacros.Domains;

public class Goal {

    private String details;
    private boolean isAchieved;
    private String goalID;

    public Goal(String details, boolean isAchieved) {
        this.details = details;
        this.isAchieved = isAchieved;
    }

    public String getDetails() {
        return details;
    }

    public boolean isAchieved() {
        return isAchieved;
    }

    public void setAchieved(boolean achieved) {
        isAchieved = achieved;
    }

    public Goal() {
    }

    public String getGoalID() {
        return goalID;
    }

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }
}

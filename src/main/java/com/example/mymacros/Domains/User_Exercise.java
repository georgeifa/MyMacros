package com.example.mymacros.Domains;

public class User_Exercise {

    private Exercise exe;
    private int reps;
    private int sets;

    public User_Exercise() {
    }

    public User_Exercise(Exercise exe, int reps, int sets) {
        this.exe = exe;
        this.reps = reps;
        this.sets = sets;
    }

    public Exercise getExe() {
        return exe;
    }

    public int getReps() {
        return reps;
    }

    public int getSets() {
        return sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }
}

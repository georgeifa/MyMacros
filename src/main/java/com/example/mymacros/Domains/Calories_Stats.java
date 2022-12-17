package com.example.mymacros.Domains;

public class Calories_Stats {

    private int eaten;
    private int burnt;

    public Calories_Stats(int eaten, int burnt) {
        this.eaten = eaten;
        this.burnt = burnt;
    }

    public Calories_Stats() {
    }

    public void setEaten(int eaten) {
        this.eaten = eaten;
    }

    public void setBurnt(int burnt) {
        this.burnt = burnt;
    }
}

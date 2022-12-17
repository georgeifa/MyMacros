package com.example.mymacros.Domains;

public class Exercise {

    private String title;
    private String category;
    private float calories_per_10_reps;

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public float getCalories_per_10_reps() {
        return calories_per_10_reps;
    }

    public Exercise() {
    }

    public Exercise(String title, String category, float calories_per_10_reps) {
        this.title = title;
        this.category = category;
        this.calories_per_10_reps = calories_per_10_reps;
    }
}

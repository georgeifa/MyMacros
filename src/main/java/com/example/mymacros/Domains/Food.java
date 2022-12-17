package com.example.mymacros.Domains;

public class Food {

    private String title;
    private int calories_per_100g;
    private float prot_per_100g;
    private float carbs_per_100g;
    private float fat_per_100g;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCalories_per_100g() {
        return calories_per_100g;
    }


    public Food() {
    }

    public Food(String title, int calories_per_100g, float prot_per_100g, float carbs_per_100g, float fat_per_100g) {
        this.title = title;
        this.calories_per_100g = calories_per_100g;
        this.prot_per_100g = prot_per_100g;
        this.carbs_per_100g = carbs_per_100g;
        this.fat_per_100g = fat_per_100g;
    }

    public float getProt_per_100g() {
        return prot_per_100g;
    }

    public float getCarbs_per_100g() {
        return carbs_per_100g;
    }

    public float getFat_per_100g() {
        return fat_per_100g;
    }
}

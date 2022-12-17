package com.example.mymacros.Domains;

public class User_Food {

    private Food food;
    private float amount;

    public Food getFood() {
        return food;
    }

    public float getAmount() {
        return amount;
    }

    public User_Food() {
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public User_Food(Food food, float amount) {
        this.food = food;
        this.amount = amount;
    }
}

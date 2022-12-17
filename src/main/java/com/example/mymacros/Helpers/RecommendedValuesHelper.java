package com.example.mymacros.Helpers;

import com.example.mymacros.Domains.UserProfile;

public class RecommendedValuesHelper {
    //use of Mifflin-St Jeor Equation
    /*
    * For men:
        BMR = 10W + 6.25H - 5A + 5
    For women:
        BMR = 10W + 6.25H - 5A - 161

    W is body weight in kg
    H is body height in cm
    A is age
    */
    private int calories,prot,carbs,fat;
    private double bmr;
    private float excMultiplier;
    private int specific_cal;
    private int cal_toBurn=500;
    private int steps;
    public void calculateRec(UserProfile userP){
                switch (userP.getGender()){
                    case "Male":
                        bmr = 10*userP.getWeight()+6.25*userP.getHeight()-5*userP.getAge()+5;
                        break;
                    case "Female":
                        bmr = 10*userP.getWeight()+6.25*userP.getHeight()-5*userP.getAge()+161;
                        break;
                }
                switch (userP.getExcFreq()){
                    case "low":
                        excMultiplier = 1.2f;
                        break;
                    case "moderate":
                        excMultiplier = 1.5f;
                        break;
                    case "high":
                        excMultiplier = 1.7f;
                        break;
                }

                bmr = bmr*excMultiplier;

                switch (userP.getGoal()){
                    case "lose":
                        bmr-=500;
                        cal_toBurn += 150;
                        break;
                    case "gain":
                        bmr+=300;
                        cal_toBurn += 100;
                        break;
                }

                prot = Math.round((float) ((0.3f*bmr)/4)); // each prot 4 calories

                carbs = Math.round((float) ((0.4f*bmr)/4)); //each carb 4 calories
                fat = Math.round((float) ((0.3f*bmr)/9)); //each carb 9 calories

                calories = (int) Math.round(bmr);
            }

    public RecommendedValuesHelper(UserProfile userP) {
        calculateRec(userP);
    }

    public RecommendedValuesHelper(UserProfile userP,String type) {
        calculateRec(userP);
        switch (type){
            case "Breakfast":
            case "Dinner":
                specific_cal = (int) Math.round(0.2f * calories);
                break;
            case "Lunch":
                specific_cal = (int) Math.round(0.4f * calories);
                break;
            case "Snacks":
                specific_cal = (int) Math.round(0.1f * calories);
                break;
        }

    }

    public int getSpecific_cal() {
        return specific_cal;
    }

    public int getCal_toBurn() {
        return cal_toBurn;
    }

    public int getCalories() {
        return calories;
    }

    public int getProt() {
        return prot;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFat() {
        return fat;
    }
}


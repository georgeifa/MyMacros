package com.example.mymacros.Domains;

import android.content.Context;
import android.graphics.Color;

import com.example.mymacros.R;

import java.util.ArrayList;

public class Action {

    private String title;
    private String tag;
    private int min_Rec;
    private int max_Rec;
    private String symbol;
    private int Image;

    public Action(String title, String tag,int min_Rec, int max_Rec, String symbol, int image) {
        this.title = title;
        this.tag = tag;
        this.min_Rec = min_Rec;
        this.max_Rec = max_Rec;
        this.symbol = symbol;
        Image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public int getMin_Rec() {
        return min_Rec;
    }

    public int getMax_Rec() {
        return max_Rec;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getImage() {
        return Image;
    }


    public Action() {
    }

    public ArrayList<Action> getAllActions(Context context, int calories_to_Eat, int calories_to_Burn){
        ArrayList<Action> actions = new ArrayList<>();

        actions.add(new Action(context.getResources().getString(R.string.add_breakfast),"Add Breakfast",Math.round((0.2f*calories_to_Eat)-150),Math.round(0.2f*calories_to_Eat),"kcal", R.drawable.ic_email));
        actions.add(new Action(context.getResources().getString(R.string.add_lunch),"Add Lunch",Math.round((0.4f*calories_to_Eat)-150),Math.round(0.4f*calories_to_Eat),"kcal", R.drawable.ic_email));
        actions.add(new Action(context.getResources().getString(R.string.add_dinner),"Add Dinner",Math.round((0.2f*calories_to_Eat)-150),Math.round(0.2f*calories_to_Eat),"kcal", R.drawable.ic_email));
        actions.add(new Action(context.getResources().getString(R.string.add_snacks),"Add Snacks",Math.round((0.1f*calories_to_Eat)-150),Math.round(0.1f*calories_to_Eat),"kcal", R.drawable.ic_email));
        actions.add(new Action(context.getResources().getString(R.string.add_exercise),"Add Exercise",calories_to_Burn-150,calories_to_Burn,"kcal", R.drawable.ic_email));

        return actions;
    }
}

package com.example.mymacros.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.Domains.Food;
import com.example.mymacros.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesHelper {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user_added_data", Context.MODE_PRIVATE);
    }

    //region Shared Preference For Exercises

    //save the new exercises
    private void save_Exe(ArrayList<Exercise> exercises)
    {
        //save the list in the file "sharedPref"
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(exercises);
        editor.putString("exercises", json);
        editor.apply();
    }

    //get the current list of exercises
    private ArrayList<Exercise> load_Exe() {
        ArrayList<Exercise> exercises;
        //get the list from the file "sharedPref"
        Gson gson = new Gson();
        String json = sharedPreferences.getString("exercises", null);
        Type type = new TypeToken<ArrayList<Exercise>>() {}.getType();
        exercises = gson.fromJson(json, type);

        if (exercises == null) {
            exercises = new ArrayList<>();
        }

        return exercises;
    }

    //save the new exercise in the list
    public void addExe(Exercise exe){
        ArrayList<Exercise> exercises = load_Exe();

        exercises.add(exe);

        save_Exe(exercises);

        Toast.makeText(context, R.string.exe_added_successfully,Toast.LENGTH_SHORT).show();
    }

    //get the list of exercises from the Shared Preferences
    public ArrayList<Exercise> get_ExeList(){
        return  load_Exe();
    }

    //endregion

    //region Shared Preference For Food

    //save the new food
    private void save_Food(ArrayList<Food> foods)
    {
        //save the list in the file "sharedPref"
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(foods);
        editor.putString("foods", json);
        editor.apply();
    }

    //get the current list of locally saved foods
    private ArrayList<Food> load_Food() {
        ArrayList<Food> foods;
        //get the list from the file "sharedPref"
        Gson gson = new Gson();
        String json = sharedPreferences.getString("foods", null);
        Type type = new TypeToken<ArrayList<Food>>() {}.getType();
        foods = gson.fromJson(json, type);

        if (foods == null) {
            foods = new ArrayList<>();
        }

        return foods;
    }

    //save the new food in the list
    public void addFood(Food food){
        ArrayList<Food> foods = load_Food();

        foods.add(food);

        save_Food(foods);

        Toast.makeText(context, R.string.fodd_added_successfully,Toast.LENGTH_SHORT).show();
    }

    //get the list of food from the Shared Preferences
    public ArrayList<Food> get_FoodList(){
        return  load_Food();
    }

    //endregion

    //save the new passcodes
    private void save_Passcode(ArrayList<String[]> passCodes)
    {
        //save the list in the file "sharedPref"
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(passCodes);
        editor.putString("passcodes", json);
        editor.apply();
    }

    //get the current list of exercises
    private ArrayList<String[]> load_Passcode() {
        ArrayList<String[]> passCodes;
        //get the list from the file "sharedPref"
        Gson gson = new Gson();
        String json = sharedPreferences.getString("passcodes", null);
        Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
        passCodes = gson.fromJson(json, type);

        if (passCodes == null) {
            passCodes = new ArrayList<>();
        }

        return passCodes;
    }

    //save the new passcode in the list
    public void addPasscode(String[] passcode){
        ArrayList<String[]> passCodes = load_Passcode();

        passCodes.add(passcode);

        save_Passcode(passCodes);

        Toast.makeText(context, R.string.passcode_added_successfully,Toast.LENGTH_SHORT).show();
    }

    public void removePasscode(String[] passcode){
        ArrayList<String[]> passCodes = load_Passcode();

        passCodes.removeIf(p -> p[0].equals(passcode[0]));

        save_Passcode(passCodes);
    }

    public String[] getPasscode(String uid){
        ArrayList<String[]> passCodes = load_Passcode();
        for(String[] p : passCodes){
            if(p[0].equals(uid))
                return p;
        }

        return null;
    }
}

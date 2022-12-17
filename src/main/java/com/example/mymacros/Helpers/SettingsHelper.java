package com.example.mymacros.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mymacros.Domains.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SettingsHelper {

    private final String userID;
    private final SharedPreferences sharedPreferences;

    public SettingsHelper(Context context,String userID)
    {
        this.userID = userID;
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    //save the new settings
    private void save_Setting(Settings settings)
    {
        //save the list in the file "sharedPref"
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(settings);
        editor.putString(userID, json);
        editor.apply();
    }

    //get the current list of exercises
    private Settings load_Settings() {
        Settings settings;
        //get the list from the file "sharedPref"
        Gson gson = new Gson();
        String json = sharedPreferences.getString(userID, null);
        Type type = new TypeToken<Settings>() {}.getType();
        settings = gson.fromJson(json, type);

        if (settings == null) {
            settings = new Settings();
            save_Setting(settings);
        }

        return settings;
    }

    //save the new exercise in the list
    public void addSettings(Settings set){
        save_Setting(set);
    }

    //get the list of exercises from the Shared Preferences
    public Settings get_Settings(){
        return  load_Settings();
    }

}

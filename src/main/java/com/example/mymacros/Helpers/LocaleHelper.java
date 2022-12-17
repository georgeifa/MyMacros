package com.example.mymacros.Helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.mymacros.Domains.UserProfile;

import java.util.Locale;

public class LocaleHelper {

    public static void setLanguage(Context context, String uid) {
        String lang;
        SettingsHelper settingsHelper = new SettingsHelper(context.getApplicationContext(), uid);
        lang = settingsHelper.get_Settings().getLanguage();

        lang = lang.isEmpty() ? Locale.ENGLISH.getLanguage() : lang;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            Resources rs = context.getResources();

            android.content.res.Configuration conf = rs.getConfiguration();
            conf.setLocale(new Locale(lang));
            context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());
        }
    }
}

package com.example.mymacros;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.mymacros.Activities.LockActivity;
import com.example.mymacros.Domains.Settings;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.Helpers.Notification_Receiver;
import com.example.mymacros.Helpers.RecommendedValuesHelper;
import com.example.mymacros.Helpers.SettingsHelper;
import com.example.mymacros.Helpers.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Application extends android.app.Application {

    private Context appContext;
    private SettingsHelper settingsHelper;

    private UserProfile userProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference dRef;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void onCreate() {
        appContext = getApplicationContext();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());
        super.onCreate();


        initializeGlobalAttributes();


    }

    public void initializeNotifications() {
        if(getSettings().isNotifications()) {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(getApplicationContext(), Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }else{
            Intent intent = new Intent(getApplicationContext(), Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }


    private void initializeGlobalAttributes() {

        //region Firebase
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        //endregion

        //region Helpers
        sharedPreferencesHelper = new SharedPreferencesHelper(getAppContext());
        //endregion
    }

    //region Global Attributes

    public Context getAppContext() {
        return appContext;
    }


    //region Firebase
    public FirebaseAuth getmAuth(){
        return mAuth;
    }

    public void setmUser(){
        mUser = mAuth.getCurrentUser();
    }

    public FirebaseUser getmUser(){
        return mUser;
    }

    public DatabaseReference getdRef(){
        return dRef;
    }
    //endregion

    //region UserProfile
    public void setUserProfile(UserProfile userProfile){
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile(){
        return userProfile;
    }
    //endregion


    //region Get The Recommended Macros
    public int[] getReccomendedMacros(String specific){
        RecommendedValuesHelper helper;
        if(specific.isEmpty()) {
            helper = new RecommendedValuesHelper(getUserProfile());
            return new int[]{
                    helper.getCalories(),
                    helper.getProt(),
                    helper.getCarbs(),
                    helper.getFat(),
                    helper.getCal_toBurn()
            };
        }
        else {
            helper = new RecommendedValuesHelper(getUserProfile(), specific);
            return new int[]{
                    helper.getCalories(),
                    helper.getProt(),
                    helper.getCarbs(),
                    helper.getFat(),
                    helper.getSpecific_cal()
            };
        }
    }
    //endregion

    //region Helpers
    public SharedPreferencesHelper getSharedPreferencesHelper(){
        return sharedPreferencesHelper;
    }

    public void setSettingsHelper(){

        settingsHelper = new SettingsHelper(getAppContext(),getmUser().getUid());

        initializeNotifications();

    }

    public SettingsHelper getSettingsHelper(){
        return settingsHelper;
    }

    public Settings getSettings(){
        return getSettingsHelper().get_Settings();
    }
    //endregion

    //endregion


    //class for APP LOCK
    class AppLifecycleListener implements DefaultLifecycleObserver {
        private boolean openLock=false;


        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            if(getmUser() != null){
                if(getSettings().isApp_lock()){
                    if(openLock) {
                        Intent intent = new Intent(getAppContext(), LockActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStop(owner);
            openLock = false;
            //if application is in the background for more than 10 secs open appLock
            new Handler().postDelayed(() -> openLock = true, 10 * 1000);

        }
    }
}







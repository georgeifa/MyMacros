package com.example.mymacros.Activities;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Settings;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.Helpers.SharedPreferencesHelper;
import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseUser;


public class SettingsActivity extends AppCompatActivity {

    private TextView app_lock_TXT,not_TXT,app_lock,logout;
    private RadioGroup language,notifications;
    private ImageView back;

    private FirebaseUser mUser;

    private Settings settings;

    private boolean startAPL;

    private Application myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        initializeComponents();
        initializeView();
        onSomethingMethods();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void initializeComponents() {
        myApp = ((Application) getApplicationContext());

        //region Findviews
        app_lock_TXT = findViewById(R.id.app_lock_en_set_TXT);
        not_TXT = findViewById(R.id.not_en_set_TXT);
        app_lock = findViewById(R.id.app_lock_settings_BTN);
        language = findViewById(R.id.lang_set_toggle);
        back = findViewById(R.id.back_setting_BTN);
        notifications = findViewById(R.id.notification_set_toggle);
        logout = findViewById(R.id.logout_settings_BTN);
        //endregion

        //region Firebase
        mUser = myApp.getmUser();
        //endregion

        settings = myApp.getSettings();

        startAPL = settings.isApp_lock();
    }

    private void initializeView() {
        if(settings.getLanguage().equals("en"))
            language.check(R.id.english_set_SWT);
        else
            language.check(R.id.greek_set_SWT);

        if(settings.isNotifications()) {
            notifications.check(R.id.not_on_set_SWT);
        }
        else {
            notifications.check(R.id.not_off_set_SWT);
        }

        setApp_btn_view();
        setVisibilityNotif();
        setVisibilityAppL();


    }

    private void onSomethingMethods() {

        back.setOnClickListener(v -> finish());

        //get the language
        language.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            if(rb.getText().toString().equals(getResources().getString(R.string.english)))
                settings.setLanguage("en");
            else
                settings.setLanguage("el");
        });

        //set notification on / off
        notifications.setOnCheckedChangeListener((group, checkedId) -> {
            settings.setNotifications(!settings.isNotifications());
            setVisibilityNotif();
        });

        //set app lock on / off
        app_lock.setOnClickListener(v -> {
            settings.setApp_lock(!settings.isApp_lock());
            setApp_btn_view();
            setVisibilityAppL();
        });

        logout.setOnClickListener(v -> Logout());
    }

    private void setVisibilityAppL() {

        if(settings.isApp_lock())
            app_lock_TXT.setVisibility(View.VISIBLE);
        else
            app_lock_TXT.setVisibility(View.GONE);
    }

    private void setApp_btn_view() {
        if(settings.isApp_lock()) {
            app_lock.setBackground(getResources().getDrawable(R.drawable.btn_bg_style));
        }
        else {
            app_lock.setBackground(getResources().getDrawable(R.drawable.edit_text_style));
        }

    }

    private void setVisibilityNotif() {

        if (settings.isNotifications()) {
            not_TXT.setVisibility(View.VISIBLE);
        }else{
            not_TXT.setVisibility(View.GONE);

        }
}

    private void saveSettings() {
        myApp.getSettingsHelper().addSettings(settings);

        if(settings.isApp_lock()!=startAPL && settings.isApp_lock())
            startActivity(new Intent(this,Lock_Register_Activity.class));
        else if(settings.isApp_lock()!=startAPL && !settings.isApp_lock()){
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
            sharedPreferencesHelper.removePasscode(sharedPreferencesHelper.getPasscode(mUser.getUid()));
        }

        myApp.initializeNotifications();

        LocaleHelper.setLanguage(this,mUser.getUid());
    }

    private void Logout(){
        myApp.getmAuth().signOut();
        Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}






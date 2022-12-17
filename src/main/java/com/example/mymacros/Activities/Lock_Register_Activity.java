package com.example.mymacros.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Settings;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.Helpers.SettingsHelper;
import com.example.mymacros.Helpers.SharedPreferencesHelper;
import com.example.mymacros.R;

public class Lock_Register_Activity extends AppCompatActivity {

    private RadioGroup enabledSWT;
    private EditText passCode;
    private ImageView fingerprint, okBTN;
    private TextView infoTXT;

    private Application myApp;

    private boolean okBTN_state = false;
    private boolean lockEnabled = true;
    private boolean fingerPrint_state = false; //true = use finger / false = don't use

    //region OnCreate - onResume
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_register);

        initializeComponents();
        onSomethingMethods();
        initializeView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // set language
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }
    //endregion

    //region Initialize Components - View / onSomethin Methods
    private void initializeComponents() {
        myApp = ((Application) getApplicationContext());

        enabledSWT = findViewById(R.id.lock_loc_REG_RG);
        passCode = findViewById(R.id.passcode_lock_REC_ETXT);
        fingerprint = findViewById(R.id.fingerprint_lock_REC_IMG);
        okBTN = findViewById(R.id.check_lock_REC_BTN);
        infoTXT = findViewById(R.id.fing_inf_lock_rec_TXT);
    }

    private void initializeView() {
        enabledSWT.check(R.id.enable_loc_REG_SWT);
    }

    private void onSomethingMethods() {
        enabledSWT.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.enable_loc_REG_SWT){
                passCode.setEnabled(true);
                infoTXT.setTextColor(Color.WHITE);
                fingerprint.setImageResource(R.drawable.ic_fingerprint);
                okBTN.setImageResource(R.drawable.ic_check_disabled);
                okBTN_state = false;
                lockEnabled = true;
            }else{
                passCode.setEnabled(false);
                passCode.clearFocus();
                passCode.setText("");
                infoTXT.setTextColor(Color.parseColor("#919191"));
                fingerprint.setImageResource(R.drawable.ic_fingerprint_disabled);
                okBTN.setImageResource(R.drawable.ic_check_enabled);
                okBTN_state = true;
                lockEnabled = false;
            }
        });

        //click on fingerprint to enable Unlocking with TouchId
        fingerprint.setOnClickListener(v -> {
            if(fingerPrint_state){
                fingerprint.setImageResource(R.drawable.ic_fingerprint);
                fingerPrint_state = false;
            }else{
                fingerprint.setImageResource(R.drawable.ic_fingerprint_enabled);
                fingerPrint_state = true;
            }
        });

        passCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //passcode must be >0 and <4
                if(s.length()>0)
                    passCode.setLetterSpacing(1.5f);
                else
                    passCode.setLetterSpacing(0f);

                if(s.length() == 4) {
                    okBTN.setImageResource(R.drawable.ic_check_enabled);
                    okBTN_state = true;
                }
                else {
                    okBTN.setImageResource(R.drawable.ic_check_disabled);
                    okBTN_state = false;
                }
            }
        });

        okBTN.setOnClickListener(v -> {
            if(okBTN_state){
                if(lockEnabled)
                    savePasscode();
                else
                    toNextActivity();
            }else{
                Toast.makeText(Lock_Register_Activity.this, R.string.passcode_must_be,Toast.LENGTH_SHORT).show();
            }
        });
    }
    //endregion

    //region saveSetting - savePasscode - go to next Activity

    private void toNextActivity() {
        //save settings (if App Lock Changed)
        //go to Main Activity
        saveSettings();

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void saveSettings() {
        //get the settings
        //save the App Lock state
        //save the settings

        Settings settings = myApp.getSettings();
        settings.setApp_lock(lockEnabled);
        SettingsHelper settingsHelper = new SettingsHelper(this, myApp.getmUser().getUid());
        settingsHelper.addSettings(settings);
    }

    private void savePasscode() {
        //save the passcode

        SharedPreferencesHelper sharedPreferences = myApp.getSharedPreferencesHelper();
        String[] passcode = new String[]{myApp.getmUser().getUid(),passCode.getText().toString(),String.valueOf(fingerPrint_state)};
        sharedPreferences.addPasscode(passcode);

        toNextActivity();
    }

    //endregion
}
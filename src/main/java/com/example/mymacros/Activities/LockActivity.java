package com.example.mymacros.Activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON;

import static com.example.mymacros.Helpers.Constants.REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Settings;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.Helpers.SharedPreferencesHelper;
import com.example.mymacros.R;

import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {

    private ImageView checkBTN,finger;
    private EditText passCode;
    private TextView alterTXT,unAvail_fing,openFing;
    private boolean method=true; //true = finger / false = passcode
    private boolean checkState=false; //true = enabled / false = disabled

    private Application myApp;

    private SharedPreferencesHelper helper;

    //region onCreate - OnResume
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);


        initializeComponents();
        initializeView();
        onSomethingMethods();
    }

    @Override
    public void onResume() {
        super.onResume();
        //set Language
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    //endregion

    //region InitializeComponents - View / OnSomethingMethods

    private void initializeComponents() {
        myApp = ((Application) getApplicationContext());


        checkBTN = findViewById(R.id.check_lock_BTN);
        passCode = findViewById(R.id.passcode_lock_ETXT);
        alterTXT = findViewById(R.id.alternative_lock_TXT);
        finger = findViewById(R.id.fingerprint_lock_IMG);
        unAvail_fing = findViewById(R.id.nofinger_lock_TXT);
        openFing = findViewById(R.id.open_finger_lock_TXT);

        helper = new SharedPreferencesHelper(this);
        if (helper.getPasscode(myApp.getmUser().getUid()) == null){
            Intent intent = new Intent(this, Lock_Register_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }


    private void initializeView() {
        finger.setVisibility(View.VISIBLE);
        passCode.setVisibility(View.GONE);
        checkBTN.setVisibility(View.GONE);
        openFing.setVisibility(View.VISIBLE);

        alterTXT.setText(R.string.or_use_passcode);

        checkForFingerprint();
    }

    private void onSomethingMethods() {
        checkBTN.setOnClickListener(v -> finish());

        alterTXT.setOnClickListener(v -> {
            if(method){
                finger.setVisibility(View.GONE);
                openFing.setVisibility(View.GONE);
                passCode.setVisibility(View.VISIBLE);
                checkBTN.setVisibility(View.VISIBLE);
                alterTXT.setText(R.string.or_use_fingerprint);
                unAvail_fing.setVisibility(View.GONE);


                method = false;

            }else{
                finger.setVisibility(View.VISIBLE);
                openFing.setVisibility(View.VISIBLE);
                passCode.setVisibility(View.GONE);
                checkBTN.setVisibility(View.GONE);
                checkForFingerprint();


                alterTXT.setText(R.string.or_use_passcode);
                method = true;
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
                if(s.length()>0)
                    passCode.setLetterSpacing(1.5f);
                else
                    passCode.setLetterSpacing(0f);

                if(s.length() == 4) {
                    checkBTN.setImageResource(R.drawable.ic_check_enabled);
                    checkState = true;
                }
                else {
                    checkBTN.setImageResource(R.drawable.ic_check_disabled);
                    checkState = false;

                }
            }
        });

        checkBTN.setOnClickListener(v -> {
            if(checkState){
                checkPass();
            }else
                Toast.makeText(getApplicationContext(), R.string.passcode_must_be, Toast.LENGTH_SHORT).show();
        });

    }
    //endregion

    //region Biometrics - Fingerprint - Passcode
    private void initializeBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("FINGERPRINT", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("FINGERPRINT", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("FINGERPRINT", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
        }

        //Create Biometrics Prompt
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LockActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if(errorCode==ERROR_NEGATIVE_BUTTON)
                    alterTXT.performClick();
                else
                    Toast.makeText(getApplicationContext(),
                        getString(R.string.authentication_error) + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        R.string.welcome_back, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.authentication_failed,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.verify_with_your_finger))
                .setNegativeButtonText(getString(R.string.use_passcode))
                .build();

        //if TouchId activated
        // prompt will appear on load

        //if successful continue to main activity
        //if fail show message
        biometricPrompt.authenticate(promptInfo);
    }

    private void checkForFingerprint() {
        //check if TouchId Unlock is enabled
        if(helper.getPasscode(myApp.getmUser().getUid())[2].equals("false")) {
            unAvail_fing.setVisibility(View.VISIBLE);
            openFing.setVisibility(View.GONE);
            finger.setOnClickListener(null);
        }
        else {
            unAvail_fing.setVisibility(View.GONE);
            openFing.setVisibility(View.VISIBLE);
            finger.setClickable(true);
            initializeBiometrics();
            finger.setOnClickListener(v -> initializeBiometrics());
        }
    }

    private void checkPass() {

        if(passCode.getText().toString().equals(helper.getPasscode(myApp.getmUser().getUid())[1])){
            finish();
        }
    }
    //endregion

}
package com.example.mymacros.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class QuizActivity extends AppCompatActivity {

    private TextView welcome;
    private EditText fName,lName,age,weight,height;
    private RadioGroup genderRG,planRG,exeRG;
    private ImageView contBTN;

    private UserProfile userP;

    private DatabaseReference dRef;
    private FirebaseUser mUser;

    private Application myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeComponents();
        initializeView();
        onSomethingMethods();
    }

    @Override
    public void onResume() {
        super.onResume();
        //set language
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    //region Initialize Components - View - OnSomethingMethods
    private void initializeComponents() {

        myApp = ((Application) getApplicationContext());

        //region Firebase
        dRef = myApp.getdRef().child("User-Details");
        mUser = myApp.getmUser();
        userP = myApp.getUserProfile();
        //endregion

        //region View textview, edittext etc

        //region TEXTVIEW

        //Welcome user TXT
        welcome = findViewById(R.id.welcome_user_quiz_TXT);

        //endregion

        //region EDITTEXT

        //Firstname ETXT
        fName = findViewById(R.id.firstname_quiz_ETXT);
        //Lastname ETXT
        lName = findViewById(R.id.lastname_quiz_ETXT);
        //Age ETXT
        age = findViewById(R.id.age_quiz_ETXT);
        //Weight ETXT
        weight = findViewById(R.id.weight_quiz_ETXT);
        //Height ETXT
        height = findViewById(R.id.height_quiz_ETXT);

        //endregion

        //region RADIOGROUP

        //Gender radiogroup
        genderRG = findViewById(R.id.gender_quiz_toggle);
        //Plan radiogroup
        planRG = findViewById(R.id.plan_quiz_toggle);
        //Exercise radiogroup
        exeRG = findViewById(R.id.exercise_quiz_toggle);

        //endregion

        //region IMAGEVIEW

        contBTN = findViewById(R.id.continue_quiz_BTN);

        //endregion

        //endregion

    }

    private void initializeView() {
        welcome.setText(userP.getUserName());
    }

    //Onclick,.. etc Methods
    private void onSomethingMethods() {
        contBTN.setOnClickListener(v -> {

            if(checkInputs()){
                setUserProfile();
                Intent intent = new Intent(QuizActivity.this, Lock_Register_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    //endregion

    //region Check If Anything Is Empty
    private boolean checkInputs() {

        if(fName.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.first_name_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(lName.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.last_name_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(age.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.age_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(weight.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.weight_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(height.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.height_empty,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    //endregion

    //set the new user profile
    private void setUserProfile() {
        userP.setFirstName(fName.getText().toString());
        userP.setLastName(lName.getText().toString());
        userP.setAge(Integer.parseInt(age.getText().toString()));
        userP.setWeight(Float.parseFloat(weight.getText().toString()));
        userP.setHeight(Float.parseFloat(height.getText().toString()));

        RadioButton rb = findViewById(genderRG.getCheckedRadioButtonId());
        userP.setGender(rb.getTag().toString());

        rb = findViewById(planRG.getCheckedRadioButtonId());
        String tmp = "";
        switch (rb.getId()){
            case R.id.lose_quiz_SWT:
                tmp = "lose";
                break;
            case R.id.maintain_quiz_SWT:
                tmp = "maintain";
                break;
            case R.id.gain_quiz_SWT:
                tmp = "gain";
                break;
        }
        userP.setGoal(tmp);

        rb = findViewById(exeRG.getCheckedRadioButtonId());
        switch (rb.getId()){
            case R.id.noExe_quiz_SWT:
                tmp = "low";
                break;
            case R.id.moderateExe_quiz_SWT:
                tmp = "moderate";
                break;
            case R.id.highExe_quiz_SWT:
                tmp = "high";
                break;
        }

        userP.setExcFreq(tmp);

        userP.setVerified(true);

        myApp.setUserProfile(userP);

        dRef.child(mUser.getUid()).setValue(userP);

        startActivity(new Intent(this, MainActivity.class));

    }

    //region MISC
    //when press back don't go to login activity
    // if presses 2 times in 3 secs exit app
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, R.string.press_back_again_to_exit,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3 * 1000);

        }
    }
    //endregion

}
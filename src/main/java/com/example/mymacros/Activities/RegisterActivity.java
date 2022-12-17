package com.example.mymacros.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    private TextView signUpButton,loginButton;
    private EditText email,name,password,password2;

    private Application myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
        onClickMethods();
    }

    private void onClickMethods() {
        //Sign Up Button
        signUpButton.setOnClickListener((view-> createUser()));

        //Log in Button
        loginButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void initializeComponents() {
        myApp = ((Application) getApplicationContext());

        //region Firebase
        mAuth =myApp.getmAuth();
        dRef = myApp.getdRef().child("User-Details");
        //endregion

        //region View Button, Edittext etc
        //Email edittext
        email = findViewById(R.id.email_Reg_ETXT);
        //Username edittext
        name = findViewById(R.id.username_Reg_ETXT);
        //Password edittext
        password = findViewById(R.id.password_Reg_ETXT);
        //Confirm Password edittext
        password2 = findViewById(R.id.conpassword_Reg_ETXT);
        //Sign Up Button
        signUpButton = findViewById(R.id.signup_reg_TXT);
        //Login Button
        loginButton = findViewById(R.id.login_Reg_TXT);

        //endregion
    }

    private void createUser(){

        if(inputsAreValid()){
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    Toast.makeText(RegisterActivity.this,"User Registered Successfully!", Toast.LENGTH_SHORT).show();
                    createUserProfile();

                }else{

                    Toast.makeText(RegisterActivity.this,"Registration Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    //Create New User Profile And Go To Next Activity
    private void createUserProfile() {
        UserProfile newUser = new UserProfile(false,name.getText().toString(),"","","",0,0,0,"","");

        setGlobalValues(newUser);
        dRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(newUser);

        Intent intent =new Intent(RegisterActivity.this, QuizActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);

    }

    private void setGlobalValues(UserProfile newUser) {
        myApp.setUserProfile(newUser);
        myApp.setSettingsHelper();
        myApp.setmUser();
    }

    //Check The Inputs Of The Fields
    private boolean inputsAreValid(){
        
        //region Check If Any Field Is Empty
        if(email.getText().toString().isEmpty()){

            Toast.makeText(RegisterActivity.this,"Email is empty!", Toast.LENGTH_SHORT).show();
            return false;

        }else if(name.getText().toString().isEmpty()) {

            Toast.makeText(RegisterActivity.this, "Username is empty!", Toast.LENGTH_SHORT).show();
            return false;

        }else if(password.getText().toString().isEmpty()){

            Toast.makeText(RegisterActivity.this,"Password is empty!", Toast.LENGTH_SHORT).show();
            return false;

        }else if(!(password.getText().toString().equals(password2.getText().toString()))) {

            Toast.makeText(RegisterActivity.this, "Passwords don't match!!", Toast.LENGTH_SHORT).show();
            return false;

        }
        //endregion
        
        return true;
    }

}
package com.example.mymacros.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Application myApp = ((Application) getApplicationContext());
        myApp.setmUser();

        //if the user currently connected is not null
        //therefore someone is already logged in
        //prompt to the main page
        if (myApp.getmUser() == null){

            new Handler().postDelayed(() -> {
                    Intent intent = new Intent(this, LoginActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                },1000);

        }else{
            myApp.setSettingsHelper();

            myApp.getdRef().child("User-Details")
                    .child(myApp.getmUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    /*check if the user is verified (has completed the quiz)
                        go to the main Activity
                        otherwise to the Quiz Activity
                    * */

                    UserProfile userP = snapshot.getValue(UserProfile.class);

                    myApp.setUserProfile(userP);

                    Intent intent;
                    if(myApp.getUserProfile() != null){
                        if(myApp.getUserProfile().isVerified())
                            intent = new Intent(LauncherActivity.this, MainActivity.class);
                        else
                            intent = new Intent(LauncherActivity.this, QuizActivity.class);
                    }else{
                        myApp.setUserProfile(new UserProfile(false,"","","","",0,0,0,"",""));
                        intent = new Intent(LauncherActivity.this, QuizActivity.class);

                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


}
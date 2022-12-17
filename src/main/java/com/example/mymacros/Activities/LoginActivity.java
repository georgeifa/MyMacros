package com.example.mymacros.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymacros.Application;
import com.example.mymacros.Dialogs.ResetPasswordDialog;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.Helpers.AccountHelpers;
import com.example.mymacros.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;


    private ImageView googleLog,twitterLog;
    private TextView loginbtn,createAcc,forgot;
    private EditText email,password;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeComponents();
        onClickMethods();

    }

    //region Initialize Components - onSomethingMethods
    private void initializeComponents() {
        Application myApp = ((Application) getApplicationContext());

        //region Firebase

        //Firebase User Authentication
        myApp.setmUser();
        mAuth = myApp.getmAuth();
        //current user
        user = myApp.getmUser();
        //Database Reference
        dRef = myApp.getdRef().child("User-Details");

        //endregion

        //region View buttons,edittext etc.

        //email edittext
        email = findViewById(R.id.email_Log_ETXT);
        //password edittext
        password= findViewById(R.id.password_Log_ETXT);
        //forgot password
        forgot = findViewById(R.id.forgotPass_login_TXT);
        //login button
        loginbtn = findViewById(R.id.login_login_TXT);
        //login with google
        googleLog = findViewById(R.id.gmail_login_BTN);
        //login with twitter
        twitterLog = findViewById(R.id.twitter_login_BTN);
        //go register button
        createAcc = findViewById(R.id.register_login_TXT);

        //endregion

        //region Additional Log-in Methods

        //GOOGLE LOGIN
        requestGoogleSignIn();
        //endregion

    }

    private void onClickMethods() {
        //if you click the "forgot password" button do:
        forgot.setOnClickListener(v -> {
            //open a dialog
            //ask for a email to send the reset link
            ResetPasswordDialog dialog = new ResetPasswordDialog(LoginActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        //PRESSED MANUAL LOGIN
        loginbtn.setOnClickListener(view -> loginUser());


        //call google sign in method
        googleLog.setOnClickListener(view -> resultLauncher.launch(new Intent((mGoogleSignInClient.getSignInIntent()))));


        //PRESSED LOGIN VIA TWITTER

        //call google sign in method
        twitterLog.setOnClickListener(view -> twitterLogin());


        //TO REGISTRATION
        createAcc.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        password.setOnTouchListener((v, event) -> new AccountHelpers().revealPass(password, event));
    }
    //endregion

    //region Third-Party Login

    //Twitter Login
    private void twitterLogin() {

        //Twitter LOGIN     //QiIJV7tdTRFxwU13vW0erFPSY lHREbkY6Wko7cROT5MMM9B5ntqb4vA2KQPWx52tZTh3GgTyz4e
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        // Target specific email with login hint.
        provider.addCustomParameter("lang", "en");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            authResult -> {
                                boolean isNew = Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser();
                                thirdPartySignIn_SignUp(isNew);
                                goToNextActivity();

                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Twitter Login Failed",e.getMessage());
                            });
        } else {
            mAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                boolean isNew = Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser();
                                thirdPartySignIn_SignUp(isNew);
                                goToNextActivity();
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Twitter Login Failed",e.getMessage());

                            });
        }
    }

    //region GOOGLE SIGN IN
    private  void requestGoogleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("630116802148-jf77j9r27k3hlq3gicd5ee7ippq1mk5j.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == Activity.RESULT_OK){
            Intent intent = result.getData();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, show message
                Toast.makeText(LoginActivity.this,"Authentication failed:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

    });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isNew = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                        thirdPartySignIn_SignUp(isNew);

                        goToNextActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this,"Authentication failed:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void thirdPartySignIn_SignUp(boolean isNew) {
        user = mAuth.getCurrentUser();
        if(isNew){
            assert user != null;
            UserProfile newUser = new UserProfile(false,user.getDisplayName(),"","","",0,0,0,"","");

            dRef.child(Objects.requireNonNull(user).getUid()).setValue(newUser);
            Toast.makeText(LoginActivity.this,"User registered successfully!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this,"User logged in successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //endregion

    //region NORMAL SIGN IN
    protected void loginUser(){

        if(email.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this,"Email is empty!", Toast.LENGTH_SHORT).show();

        }else if(password.getText().toString().isEmpty()){

            Toast.makeText(LoginActivity.this,"Password is empty!", Toast.LENGTH_SHORT).show();

        }else{
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this,"User logged in successfully!", Toast.LENGTH_SHORT).show();
                    user = mAuth.getCurrentUser();
                    goToNextActivity();
                }else{
                    Toast.makeText(LoginActivity.this,"Log in Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //endregion

    private void goToNextActivity() {
        //Upon Login Check if user is verified (Completed task)
        //if he is go to main activity
        //if not go to Quiz Activity

        Application myApp = ((Application) getApplicationContext());

        myApp.setmUser();


        dRef.child(myApp.getmUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userP = snapshot.getValue(UserProfile.class);

                myApp.setSettingsHelper();
                myApp.setUserProfile(userP);

                Intent intent;
                if(myApp.getUserProfile() != null){
                    if(myApp.getUserProfile().isVerified())
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    else
                        intent = new Intent(LoginActivity.this, QuizActivity.class);
                }else{
                    myApp.setUserProfile(new UserProfile(false,"","","","",0,0,0,"",""));
                    intent = new Intent(LoginActivity.this, QuizActivity.class);

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
package com.example.mymacros.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mymacros.Activities.MainActivity;
import com.example.mymacros.Activities.SettingsActivity;
import com.example.mymacros.Application;
import com.example.mymacros.Dialogs.ReAuthDialog;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.Helpers.AccountHelpers;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccountFragment extends Fragment {

    private TextView saveAcc,cancelAcc,savePer,cancelPer,usernameTXT,emailTXT,fullnameTXT;
    private EditText username,email,password;
    private EditText firstN,lastN, age, height,weight;
    private RadioGroup gender,plan,exercise;
    private RadioButton rb;
    private ImageView profile,settings;
    private View acD,pD;

    private DatabaseReference dRef;
    private FirebaseUser mUser;
    private StorageReference mStorage;

    private UserProfile userP;

    private Application myApp;

    private Context context;
    private boolean settingsOpened = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        initializeComponents(view);
        initializeView(view);
        onSomethingMethods(view);
        return view;
    }

    //region Initializations

    private void initializeComponents(View view) {
        myApp = ((Application) requireActivity().getApplicationContext());
        context = getContext();
        //region Firebase

        dRef = myApp.getdRef().child("User-Details");
        mUser = myApp.getmUser();
        userP = myApp.getUserProfile();
        mStorage = FirebaseStorage.getInstance().getReference().child("users/"+mUser.getUid()+"/profile.jpg");;


        //endregion

        //region findViews
        saveAcc = view.findViewById(R.id.save_changes_acc_BTN);
        cancelAcc = view.findViewById(R.id.cancel_changes_acc_BTN);
        savePer = view.findViewById(R.id.save_changes_per_acc_BTN);
        cancelPer = view.findViewById(R.id.cancel_changes_per_acc_BTN);
        username = view.findViewById(R.id.username_acc_ETXT);
        usernameTXT = view.findViewById(R.id.username_acc_TXT);
        emailTXT = view.findViewById(R.id.email_acc_TXT);
        email = view.findViewById(R.id.email_acc_ETXT);
        fullnameTXT = view.findViewById(R.id.fullname_acc_TXT);
        firstN = view.findViewById(R.id.firstname_acc_ETXT);
        lastN = view.findViewById(R.id.lastname_acc_ETXT);
        age = view.findViewById(R.id.age_acc_ETXT);
        weight = view.findViewById(R.id.weight_acc_ETXT);
        height = view.findViewById(R.id.height_acc_ETXT);
        password = view.findViewById(R.id.password_acc_ETXT);

        gender = view.findViewById(R.id.gender_acc_toggle);
        plan = view.findViewById(R.id.plan_acc_toggle);
        exercise = view.findViewById(R.id.exercise_acc_toggle);

        settings = view.findViewById(R.id.settings_acc_BTN);
        profile = view.findViewById(R.id.profile_acc_IMG);

        acD = view.findViewById(R.id.acd_account_LL);
        pD = view.findViewById(R.id.pd_account_LL);

        //endregion
    }

    private void initializeView(View view) {

        //region Textview Details
        usernameTXT.setText(userP.getUserName());
        emailTXT.setText(mUser.getEmail());
        fullnameTXT.setText(userP.getFirstName() + " " + userP.getLastName());
        //endregion

        //region Account Details
        username.setText(userP.getUserName());
        email.setText(mUser.getEmail());

        //endregion

        //region Profile Details

        //region EDITTEXTS
        firstN.setText(userP.getFirstName());
        lastN.setText(userP.getLastName());
        age.setText(String.valueOf(userP.getAge()));
        height.setText(String.valueOf(userP.getHeight()));
        weight.setText(String.valueOf(userP.getWeight()));
        //endregion

        //region RADIOGROUPS

        //region GENDER
        switch (userP.getGender()){
            case "Female":
                rb = view.findViewById(R.id.female_acc_SWT);
                break;
            case "Male":
                rb = view.findViewById(R.id.male_acc_SWT);
                break;
        }
        gender.check(rb.getId());
        //endregion

        //region PLAN
        switch (userP.getGoal()){
            case "lose":
                rb = view.findViewById(R.id.lose_acc_SWT);
                break;
            case "maintain":
                rb = view.findViewById(R.id.maintain_acc_SWT);
                break;
            case "gain":
                rb = view.findViewById(R.id.gain_acc_SWT);
                break;
        }
        plan.check(rb.getId());
        //endregion

        //region EXERCISE FREQ

        switch (userP.getExcFreq()){
            case "low":
                rb = view.findViewById(R.id.noExe_acc_SWT);
                break;
            case "moderate":
                rb = view.findViewById(R.id.moderateExe_acc_SWT);
                break;
            case "high":
                rb = view.findViewById(R.id.highExe_acc_SWT);
                break;
        }
        exercise.check(rb.getId());

        //endregion
        //endregion

        //region Set Visibility
        disableBTNAcc(view);
        disableBTNPer(view);
        //endregion

        if(!new AccountHelpers().getLoginProvider(mUser).equals(FirebaseAuthProvider.PROVIDER_ID)){
            password.setVisibility(View.GONE);
            email.setEnabled(false);
            email.setInputType(InputType.TYPE_NULL);
        }
        //endregion


        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profile);
            }
        });
    }

    //endregion

    //region Enable-Disable Buttons Methods
    private void disableBTNPer(View view) {
        pD.setVisibility(View.GONE);
        view.clearFocus();
    }

    private void disableBTNAcc(View view) {
        acD.setVisibility(View.GONE);
        view.clearFocus();
    }

    private void enableBtnPer() {
        pD.setVisibility(View.VISIBLE);
    }

    private void enableBtnACC() {
        acD.setVisibility(View.VISIBLE);
    }
    //endregion

    //region Save Methods

    @SuppressLint("NonConstantResourceId")
    private void saveProfileDetails(View v) {
        userP.setFirstName(firstN.getText().toString());
        userP.setLastName(lastN.getText().toString());
        userP.setAge(Integer.parseInt(age.getText().toString()));
        userP.setWeight(Float.parseFloat(weight.getText().toString()));
        saveWeight();
        userP.setHeight(Float.parseFloat(height.getText().toString()));

        RadioButton rb = v.findViewById(gender.getCheckedRadioButtonId());
        userP.setGender(rb.getTag().toString());

        rb = v.findViewById(plan.getCheckedRadioButtonId());
        String tmp = "";
        switch (rb.getId()){
            case R.id.lose_acc_SWT:
                tmp = "lose";
                break;
            case R.id.maintain_acc_SWT:
                tmp = "maintain";
                break;
            case R.id.gain_acc_SWT:
                tmp = "gain";
                break;
        }
        userP.setGoal(tmp);

        rb = v.findViewById(exercise.getCheckedRadioButtonId());
        switch (rb.getId()){
            case R.id.noExe_acc_SWT:
                tmp = "low";
                break;
            case R.id.moderateExe_acc_SWT:
                tmp = "moderate";
                break;
            case R.id.highExe_acc_SWT:
                tmp = "high";
                break;
        }

        userP.setExcFreq(tmp);

        userP.setVerified(true);

        myApp.setUserProfile(userP);
        dRef.child(mUser.getUid()).setValue(userP);

        resetView();
    }

    private void saveWeight() {
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);
        myApp.getdRef().child("Stats").child(mUser.getUid()).child("Weight").child(df.format(Calendar.getInstance().getTime())).setValue(userP.getWeight());
    }

    private void saveAccountDetails() {

        userP.setUserName(username.getText().toString());

        ReAuthDialog cdd = new ReAuthDialog(context);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();

        cdd.findViewById(R.id.confirm_dialog_reAuth_BTN).setOnClickListener(v -> {
            if(!email.getText().toString().equals(mUser.getEmail()))
                cdd.updateEmail(email);
            if(!password.getText().toString().isEmpty())
                cdd.updatePassword(password);
            cdd.dismiss();


            new Handler().postDelayed(this::resetView, 1000);


        });

        cdd.findViewById(R.id.cancel_dialog_reAuth_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdd.dismiss();
                Toast.makeText(context,"Re-Authoriziation Cancelled",Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        if(settingsOpened)
            resetView();
    }

    //endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                profile.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        mStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void onSomethingMethods(View view) {

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        //region Save - Cancel Buttons
        saveAcc.setOnClickListener(v -> {
            if(checkAccInputs()){
                saveAccountDetails();
            }
        });

        cancelAcc.setOnClickListener(v -> {
            initializeView(view);
            disableBTNAcc(view);
        });

        savePer.setOnClickListener(v -> {
            if(checkProfInputs()){
                saveProfileDetails(view);
            }
        });

        cancelPer.setOnClickListener(v -> {
            initializeView(view);
            disableBTNPer(view);
        });
        //endregion

        //region Account Details
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if new text != from saved text
                enableBtnACC();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if new text != from saved text
                enableBtnACC();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if new text != from saved text
                enableBtnACC();
            }
        });


        password.setOnTouchListener((v, event) -> new AccountHelpers().revealPass(password, event));
        //endregion

        //region Personal Details
        //region Edittexts
        firstN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(userP.getFirstName()))
                    enableBtnPer();
                else
                    disableBTNPer(view);
            }
        });

        lastN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(userP.getLastName()))
                    enableBtnPer();
                else
                    disableBTNPer(view);
            }
        });

        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(String.valueOf(userP.getAge())))
                    enableBtnPer();
                else
                    disableBTNPer(view);
            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(String.valueOf(userP.getWeight())))
                    enableBtnPer();
                else
                    disableBTNPer(view);
            }
        });

        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(String.valueOf(userP.getHeight())))
                    enableBtnPer();
                else
                    disableBTNPer(view);
            }
        });
        //endregion

        //region RadioGroups
        gender.setOnCheckedChangeListener((group, checkedId) -> {
            rb = view.findViewById(checkedId);
            if(!rb.getTag().toString().equals(userP.getGender()))
                enableBtnPer();
            else
                disableBTNPer(view);
        });

        plan.setOnCheckedChangeListener((group, checkedId) -> {
            rb = view.findViewById(checkedId);
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
            if(!tmp.equals(userP.getGoal()))
                enableBtnPer();
            else
                disableBTNPer(view);

        });

        exercise.setOnCheckedChangeListener((group, checkedId) -> {
            rb = view.findViewById(checkedId);
            String tmp = "";
            switch (rb.getId()) {
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
            if(!tmp.equals(userP.getExcFreq()))
                enableBtnPer();
            else
                disableBTNPer(view);

        });
        //endregion
        //endregion

        //region Settings
        settings.setOnClickListener(v -> {
            //go to setting activity
            settingsOpened = true;
            startActivity(new Intent(context, SettingsActivity.class));
        });
        //endregion
    }

    //region MISC

    private void resetView() {
        FragmentTransaction tr = requireFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, new AccountFragment());
        tr.commit();
    }

    private boolean checkProfInputs() {
        if(firstN.getText().toString().isEmpty()){
            Toast.makeText(context,R.string.first_name_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(lastN.getText().toString().isEmpty()){
            Toast.makeText(context,R.string.last_name_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(age.getText().toString().isEmpty()){
            Toast.makeText(context,R.string.age_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(weight.getText().toString().isEmpty()){
            Toast.makeText(context,R.string.weight_empty,Toast.LENGTH_SHORT).show();
            return false;
        }else if(height.getText().toString().isEmpty()){
            Toast.makeText(context,R.string.height_empty,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkAccInputs() {
        if(username.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.username_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(email.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.email_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    //endregion

}
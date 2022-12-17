package com.example.mymacros.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymacros.Application;
import com.example.mymacros.Helpers.AccountHelpers;
import com.example.mymacros.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ReAuthDialog extends Dialog {

    private EditText password;

    private final Context context;
    private FirebaseUser user;

    private final String prov;
    private final Application myApp;

    public ReAuthDialog(@NonNull Context context) {

        super(context);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());
        user = myApp.getmUser();
        prov = new AccountHelpers().getLoginProvider(user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reauthenticate);

        password = findViewById(R.id.password_reAuth_ETXT);

        password.setOnTouchListener((v, event) -> new AccountHelpers().revealPass(password, event));


    }

    public void updatePassword(EditText newPass){

        if (prov.equals(FirebaseAuthProvider.PROVIDER_ID)) {

            AuthCredential credential = EmailAuthProvider
                    .getCredential(Objects.requireNonNull(user.getEmail()), password.getText().toString());

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    myApp.setmUser();
                                    Toast.makeText(context, R.string.password_updated, Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, R.string.new_password_is_not_valid, Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(context, R.string.authentication_failed, Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    public void updateEmail(EditText newEmail){
        AuthCredential credential;
        if (FirebaseAuthProvider.PROVIDER_ID.equals(prov)) {
            //region Firebase Login
            credential = EmailAuthProvider
                    .getCredential(Objects.requireNonNull(user.getEmail()), password.getText().toString());

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            user.updateEmail(newEmail.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    myApp.setmUser();
                                    Toast.makeText(context, R.string.email_updated, Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, R.string.new_email_is_not_valid, Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(context, R.string.authentication_failed, Toast.LENGTH_SHORT).show();

                        }
                    });
            //endregion
        }
    }

}

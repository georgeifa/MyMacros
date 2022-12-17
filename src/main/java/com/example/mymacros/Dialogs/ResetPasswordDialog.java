package com.example.mymacros.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymacros.Application;
import com.example.mymacros.R;

public class ResetPasswordDialog extends Dialog implements android.view.View.OnClickListener {

    private EditText email;

    private final Context context;

    private final Application myApp;

    public ResetPasswordDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reset_password);

        email = findViewById(R.id.email_resetP_ETXT);
        TextView confirm = findViewById(R.id.confirm_dialog_resetP_BTN);
        TextView cancel = findViewById(R.id.cancel_dialog_resetP_BTN);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_dialog_resetP_BTN:
                sendLink(); //add goal
                break;
            case R.id.cancel_dialog_resetP_BTN:
                Toast.makeText(context, R.string.reset_link_not_sent, Toast.LENGTH_SHORT).show();
                cancel();
                break;
            default:
                break;
        }
    }

    //if you press "yes" to send the link
    //try to sent the link to the email
    //if it manages to sent the link show a confirmation message
    private void sendLink() {
        String mail = email.getText().toString();
        myApp.getmAuth().sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(context, R.string.reset_link_sent, Toast.LENGTH_SHORT).show();
                dismiss();
            }else{
                Toast.makeText(context, R.string.check_your_email_and_try_again, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

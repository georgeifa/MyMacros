package com.example.mymacros.Helpers;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;

public class AccountHelpers {

    public boolean revealPass(EditText password, MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width() - password.getPaddingEnd())) {
                password.setInputType(InputType.TYPE_NULL);
                password.setEnabled(false);
                if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    //Show Password
                    password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_hide_password,0);

                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    //Hide Password
                    password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password,0,R.drawable.ic_show_password,0);

                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }

                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setEnabled(true);

                return true;
            }
        }
        return false;
    }

    public String getLoginProvider(FirebaseUser user){
        for( UserInfo u: user.getProviderData()){
            switch (u.getProviderId()){
                case GoogleAuthProvider.PROVIDER_ID:
                    return GoogleAuthProvider.PROVIDER_ID;
                case TwitterAuthProvider.PROVIDER_ID:
                    return TwitterAuthProvider.PROVIDER_ID;
            }
        }

        return FirebaseAuthProvider.PROVIDER_ID;
    }
}

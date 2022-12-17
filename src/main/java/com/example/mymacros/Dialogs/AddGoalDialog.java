package com.example.mymacros.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Goal;
import com.example.mymacros.R;
import com.google.firebase.database.DatabaseReference;

public class AddGoalDialog extends Dialog implements android.view.View.OnClickListener {

    private TextView yes, no, characterCountTXT;
    private EditText n_goal_ETXT;

    private DatabaseReference dRef;

    private final Context context;

    private final Application myApp;

    public AddGoalDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_goal);

        initializeComponents();

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        onWriteMethods();
    }

    private void initializeComponents() {

        //region Firebase
        dRef = myApp.getdRef().child("Goals").child(myApp.getmUser().getUid());
        //endregion

        //region Findviews
        yes = findViewById(R.id.add_dialog_add_goal_BTN);
        no = findViewById(R.id.cancel_add_goal_BTN);
        characterCountTXT = findViewById(R.id.character_written_dialog_add_goal_TXT);
        n_goal_ETXT = findViewById(R.id.add_goal_add_goal_dialog_ETXT);
        //endregion
    }

    private void onWriteMethods() {
        n_goal_ETXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                characterCountTXT.setText(String.valueOf(s.length()));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_dialog_add_goal_BTN:
                addGoal(); //add goal
                break;
            case R.id.cancel_add_goal_BTN:
                cancel();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void addGoal() {
        Goal goal = new Goal(n_goal_ETXT.getText().toString(),false);

        dRef.push().setValue(goal).addOnSuccessListener(unused -> {
            Toast.makeText(context, R.string.goal_added_successfully, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}


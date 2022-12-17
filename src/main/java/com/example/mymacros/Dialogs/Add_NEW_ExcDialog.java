package com.example.mymacros.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Add_NEW_ExcDialog extends Dialog{

    private TextView characterCountTXT;

    public TextView add, cancel;

    private EditText exeTitle,caloriesPer10;
    private AutoCompleteTextView catTXT;
    private final Context context;

    private final Application myApp;


    public Add_NEW_ExcDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_exc);

        initializeComponents();

        onWriteMethods();
    }

    private void initializeComponents() {
        //region Findviews
        add = findViewById(R.id.add_dialog_add_exc_BTN);
        cancel = findViewById(R.id.cancel_add_exc_BTN);
        characterCountTXT = findViewById(R.id.character_title_written_dialog_add_exc_TXT);
        caloriesPer10 = findViewById(R.id.calories_add_exc_ETXT);
        exeTitle = findViewById(R.id.title_add_exc_dialog_ETXT);
        catTXT = findViewById(R.id.autoCompleteTextView);
        //endregion

        //region Dropdown Textview
        String[] categories = context.getResources().getStringArray(R.array.exercise_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.dropdown_exe_cat_item,categories);
        catTXT.setAdapter(adapter);
        //endregion
    }


    private void onWriteMethods() {
        exeTitle.addTextChangedListener(new TextWatcher() {
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

    public void addExercise() {
        if(checkInput()) {
            Exercise exe = new Exercise(exeTitle.getText().toString(),
                    catTXT.getText().toString(),
                    Float.parseFloat(caloriesPer10.getText().toString()));


            myApp.getdRef().child("Exercises").child(exe.getCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.requireNonNull(snap.getValue(Exercise.class)).getTitle().equals(exe.getTitle()))
                            exists = true;
                    }
                    if (exists)
                        Toast.makeText(context, R.string.exe_name_exists, Toast.LENGTH_SHORT).show();
                    else {
                        myApp.getSharedPreferencesHelper().addExe(exe);
                        dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean checkInput() {
        if(exeTitle.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.exe_title_empty, Toast.LENGTH_SHORT).show();
            return false;
        }else if(caloriesPer10.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.calories_burnt_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}

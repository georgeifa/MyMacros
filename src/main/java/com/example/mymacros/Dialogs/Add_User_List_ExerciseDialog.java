package com.example.mymacros.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Calories_Stats;
import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.Domains.User_Exercise;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Add_User_List_ExerciseDialog extends Dialog {

    private final Context context;

    private DatabaseReference dRef;

    private TextView calories;
    public EditText reps,sets;
    public TextView add,cancel;

    private final Exercise exercise;
    private final String meal;

    private final Application myApp;

    public Add_User_List_ExerciseDialog(@NonNull Context context, Exercise exercise, String meal) {
        super(context);
        this.context = context;
        this.exercise = exercise;
        this.meal = meal;
        myApp = ((Application) context.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_user_exercise);

        initializeComponents();
        onSomethingMethods();
    }

    private void onSomethingMethods() {
        reps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sets.getText().length()>0) {
                    add.setEnabled(Float.parseFloat(s.toString()) > 0);
                    calories.setText(String.valueOf(
                            ((Integer.parseInt(s.toString())*Integer.parseInt(sets.getText().toString()))*exercise.getCalories_per_10_reps())/10));

                }
            }
        });

        sets.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(reps.getText().length()>0) {
                    add.setEnabled(Float.parseFloat(s.toString()) > 0);
                    calories.setText(String.valueOf(
                            ((Integer.parseInt(s.toString())*Integer.parseInt(reps.getText().toString()))*exercise.getCalories_per_10_reps())/10));

                }
            }
        });
    }

    private void initializeComponents() {

        //region FindViews
        add = findViewById(R.id.add_user_exe_dialog_BTN);
        cancel = findViewById(R.id.cancel_user_exe_dialog_BTN);
        TextView title = findViewById(R.id.title_user_exe_dialog_TXT);
        calories = findViewById(R.id.calories_user_exe_dialog_TXT);
        TextView category = findViewById(R.id.cat_user_exe_dialog_ETXT);
        reps = findViewById(R.id.reps_user_exe_dialog_ETXT);
        sets = findViewById(R.id.sets_user_exe_dialog_ETXT);

        //endregion

        title.setText(exercise.getTitle());
        category.setText(exercise.getCategory());

        dRef = myApp.getdRef();


    }

    public void add_To_List(String date){
        User_Exercise f = new User_Exercise(exercise,Integer.parseInt(reps.getText().toString()),Integer.parseInt(sets.getText().toString()));

        dRef.child("User_Record").child(myApp.getmUser().getUid()).child(date).child(meal).child(f.getExe().getTitle())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue(User_Exercise.class)!=null) {
                            User_Exercise ue = snapshot.getValue(User_Exercise.class);
                            f.setReps(f.getReps()+ue.getReps());
                            f.setSets(f.getSets()+ue.getSets());
                        }

                        dRef.child("User_Record").child(myApp.getmUser().getUid()).child(date).child(meal).child(f.getExe().getTitle()).setValue(f)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                        Toast.makeText(context, R.string.add_to_your_list, Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, R.string.failed_to_add_to_your_list, Toast.LENGTH_SHORT).show();
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}

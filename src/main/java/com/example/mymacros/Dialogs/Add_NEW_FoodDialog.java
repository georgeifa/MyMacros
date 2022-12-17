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
import com.example.mymacros.Domains.Food;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Add_NEW_FoodDialog extends Dialog{

    private TextView characterCountTXT;
    public TextView yes, no;
    private EditText foodTitle,prot,carbs,fat,calories;

    private final Context context;

    private final Application myApp;

    public Add_NEW_FoodDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_food);

        initializeComponents();

        onWriteMethods();

    }

    private void initializeComponents() {

        //region FindViews
        yes = findViewById(R.id.add_dialog_add_food_BTN);
        no = findViewById(R.id.cancel_add_food_BTN);
        characterCountTXT = findViewById(R.id.character_title_written_dialog_add_food_TXT);
        foodTitle = findViewById(R.id.title_add_food_dialog_ETXT);
        calories = findViewById(R.id.calories_add_food_ETXT);
        prot = findViewById(R.id.protein_add_food_dialog_ETXT);
        carbs = findViewById(R.id.carb_add_food_dialog_ETXT);
        fat = findViewById(R.id.fat_add_food_dialog_ETXT);
        //endregion

    }

    private void onWriteMethods() {
        foodTitle.addTextChangedListener(new TextWatcher() {
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


    public void addFood() {
        if(checkInput()){
            Food food = new Food(foodTitle.getText().toString(),
                    Integer.parseInt(calories.getText().toString()),
                    Float.parseFloat(prot.getText().toString()),
                    Float.parseFloat(carbs.getText().toString()),
                    Float.parseFloat(fat.getText().toString()));

            myApp.getdRef().child("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(Objects.requireNonNull(snap.getValue(Food.class)).getTitle().equals(food.getTitle()))
                            exists = true;
                    }
                    if(exists)
                        Toast.makeText(context, R.string.food_name_exists,Toast.LENGTH_SHORT).show();
                    else{
                        myApp.getSharedPreferencesHelper().addFood(food);
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
        if(foodTitle.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.food_title_empty, Toast.LENGTH_SHORT).show();
            return false;
        }else if(calories.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.food_calories_empty, Toast.LENGTH_SHORT).show();
            return false;
        }else if(prot.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.food_prot_empty, Toast.LENGTH_SHORT).show();
            return false;
        }else if(carbs.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.food_carbs_empty, Toast.LENGTH_SHORT).show();
            return false;
        }else if(fat.getText().toString().isEmpty()) {
            Toast.makeText(context, R.string.food_fats_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }
}

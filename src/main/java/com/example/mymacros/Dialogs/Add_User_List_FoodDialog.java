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
import com.example.mymacros.Domains.User_Food;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Add_User_List_FoodDialog extends Dialog{

    private final Context context;

    private DatabaseReference dRef;

    private TextView calories;
    private TextView prot;
    private TextView carbs;
    private TextView fat;
    public EditText amount;
    public TextView add,cancel;
    private final Food food;
    private final String meal;

    private final Application myApp;

    public Add_User_List_FoodDialog(@NonNull Context context, Food food, String meal) {
        super(context);
        this.context = context;
        this.food = food;
        this.meal = meal;
        myApp = ((Application) context.getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_user_food);

        initializeComponents();
        onSomethingMethods();
    }

    private void onSomethingMethods() {
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calories.setText(String.valueOf((Float.parseFloat(s.toString())*food.getCalories_per_100g())/100));
                prot.setText(String.valueOf((Float.parseFloat(s.toString())*food.getProt_per_100g())/100));
                carbs.setText(String.valueOf((Float.parseFloat(s.toString())*food.getCarbs_per_100g())/100));
                fat.setText(String.valueOf((Float.parseFloat(s.toString())*food.getFat_per_100g())/100));

                add.setEnabled(Float.parseFloat(s.toString()) > 0);
            }
        });
    }

    private void initializeComponents() {

        //region FindViews
        add = findViewById(R.id.add_user_food_dialog_BTN);
        cancel = findViewById(R.id.cancel_user_food_dialog_BTN);
        TextView title = findViewById(R.id.title_user_food_dialog_TXT);
        calories = findViewById(R.id.calories_user_food_dialog_TXT);
        prot = findViewById(R.id.protein_user_food_dialog_TXT);
        carbs = findViewById(R.id.carb_user_food_dialog_TXT);
        fat = findViewById(R.id.fat_user_food_dialog_TXT);

        amount = findViewById(R.id.amount_user_food_dialog_ETXT);
        //endregion

        title.setText(food.getTitle());

        dRef = myApp.getdRef().child("User_Record").child(myApp.getmUser().getUid());


    }

    public void add_To_List(String date){
        User_Food f = new User_Food(food,Float.parseFloat(amount.getText().toString()));
        dRef.child(date).child(meal).child(f.getFood().getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(User_Food.class)!=null){
                    User_Food uF = snapshot.getValue(User_Food.class);
                    f.setAmount(Objects.requireNonNull(uF).getAmount() + f.getAmount());
                }

                dRef.child(date).child(meal).child(f.getFood().getTitle()).setValue(f)
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

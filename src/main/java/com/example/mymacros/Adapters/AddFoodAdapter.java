package com.example.mymacros.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Food;
import com.example.mymacros.Domains.User_Food;
import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFoodAdapter extends RecyclerView.Adapter<AddFoodAdapter.MyViewHolder>{


    Context context;
    ArrayList<User_Food> list;

    private final DatabaseReference dRef;

    public AddFoodAdapter(Context context, ArrayList<User_Food> list,String date,String meal) {
        this.context = context;
        this.list = list;
        Application myApp = ((Application) context.getApplicationContext());
        dRef = myApp.getdRef().child("User_Record").child(myApp.getmUser().getUid())
                .child(date).child(meal);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_added_food,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User_Food uF = list.get(position);
        Food food = uF.getFood();
        holder.title.setText(food.getTitle());
        holder.calories.setText(String.valueOf((food.getCalories_per_100g()*uF.getAmount())/100));
        holder.amount.setText(String.valueOf(uF.getAmount()));

        holder.delete_BTN.setOnClickListener(v -> dRef.child(food.getTitle()).removeValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,calories,amount;
        ImageView delete_BTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_food_viewH_TXT);
            calories = itemView.findViewById(R.id.calories_food_viewH_TXT);
            amount = itemView.findViewById(R.id.amount_food_viewH_TXT);
            delete_BTN = itemView.findViewById(R.id.remove_food_viewH_BTN);
        }
    }
}

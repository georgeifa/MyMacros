package com.example.mymacros.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymacros.Dialogs.Add_User_List_FoodDialog;
import com.example.mymacros.Domains.Food;
import com.example.mymacros.R;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder>{

    private final Context context;
    private final ArrayList<Food> list;
    private final String meal;
    private final String date;

    public FoodListAdapter(Context context, ArrayList<Food> list,String date, String meal) {
        this.context = context;
        this.list = list;
        this.meal = meal;
        this.date = date;

    }

    @NonNull
    @Override
    public FoodListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_food,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.MyViewHolder holder, int position) {

        Food food = list.get(position);
        holder.title.setText(food.getTitle());
        holder.calories.setText(String.valueOf(food.getCalories_per_100g()));
        holder.protein.setText(String.valueOf(food.getProt_per_100g()));
        holder.carbs.setText(String.valueOf(food.getCarbs_per_100g()));
        holder.fat.setText(String.valueOf(food.getFat_per_100g()));

        holder.add_BTN.setOnClickListener(v -> {

            Add_User_List_FoodDialog cdd = new Add_User_List_FoodDialog(v.getContext(),food,meal);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

            cdd.add.setOnClickListener(v1 -> {
                cdd.add_To_List(date);
                cdd.dismiss();
            });


            cdd.cancel.setOnClickListener(v12 -> cdd.cancel());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,calories,protein,carbs,fat;
        ImageView add_BTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_view_food_TXT);
            calories = itemView.findViewById(R.id.calories_view_food_TXT);
            protein = itemView.findViewById(R.id.prot_view_food_TXT);
            carbs = itemView.findViewById(R.id.carbs_view_food_TXT);
            fat = itemView.findViewById(R.id.fat_view_food_TXT);
            add_BTN = itemView.findViewById(R.id.add_view_food_BTN);
        }
    }
}

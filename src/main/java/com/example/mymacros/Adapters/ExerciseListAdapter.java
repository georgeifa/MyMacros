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

import com.example.mymacros.Dialogs.Add_User_List_ExerciseDialog;
import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.R;

import java.util.ArrayList;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.MyViewHolder>{
    private final Context context;
    private final ArrayList<Exercise> list;
    private final String meal;
    private final String date;

    public ExerciseListAdapter(Context context, ArrayList<Exercise> list,String date, String meal) {
        this.context = context;
        this.list = list;
        this.meal = meal;
        this.date = date;
    }

    @NonNull
    @Override
    public ExerciseListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_exercise,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.MyViewHolder holder, int position) {

        Exercise exercise = list.get(position);
        holder.title.setText(exercise.getTitle());
        holder.category.setText(String.valueOf(exercise.getCategory()));

        holder.add_BTN.setOnClickListener(v -> {

            Add_User_List_ExerciseDialog cdd = new Add_User_List_ExerciseDialog(v.getContext(),exercise,meal);
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
        TextView title,category;
        ImageView add_BTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_view_exe_TXT);
            category = itemView.findViewById(R.id.category_view_exe_TXT);
            add_BTN = itemView.findViewById(R.id.add_view_exe_BTN);
        }
    }
}

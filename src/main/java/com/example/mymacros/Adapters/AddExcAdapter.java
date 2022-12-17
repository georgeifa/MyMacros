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
import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.Domains.User_Exercise;
import com.example.mymacros.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AddExcAdapter extends RecyclerView.Adapter<AddExcAdapter.MyViewHolder>{

    private final Context context;
    private final ArrayList<User_Exercise> list;

    private final DatabaseReference dRef;

    public AddExcAdapter(Context context, ArrayList<User_Exercise> list,String date,String meal) {
        this.context = context;
        this.list = list;
        Application myApp = ((Application) context.getApplicationContext());
        dRef = myApp.getdRef().child("User_Record").child(myApp.getmUser().getUid())
                .child(date).child(meal);
    }

    @NonNull
    @Override
    public AddExcAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_added_excersice,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddExcAdapter.MyViewHolder holder, int position) {

        //set the items text values (title, calories, sets n reps)

        User_Exercise uE = list.get(position);
        Exercise exercise = uE.getExe();
        holder.title.setText(exercise.getTitle());
        holder.calories.setText(String.valueOf((exercise.getCalories_per_10_reps()*(uE.getReps()*uE.getSets()))/10));
        holder.reps.setText(String.valueOf(uE.getReps()));
        holder.sets.setText(String.valueOf(uE.getSets()));

        holder.delete_BTN.setOnClickListener(v -> dRef.child(exercise.getTitle()).removeValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title,calories,reps,sets;
        ImageView delete_BTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_exc_viewH_TXT);
            calories = itemView.findViewById(R.id.kcal_burnt_exc_TXT);
            reps = itemView.findViewById(R.id.rep_num_exc_TXT);
            sets = itemView.findViewById(R.id.set_num_exc_TXT);
            delete_BTN = itemView.findViewById(R.id.remove_exc_viewH_BTN);
        }
    }
}

package com.example.mymacros.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymacros.Activities.AddExcActivity;
import com.example.mymacros.Activities.AddFoodActivity;
import com.example.mymacros.Domains.Action;
import com.example.mymacros.R;

import java.util.ArrayList;

public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.MyViewHolder>{

    private final Context context;
    private final ArrayList<Action> list;
    private final String date;

    public ActionsAdapter(Context context, ArrayList<Action> list, String date) {
        this.context = context;
        this.list = list;
        this.date = date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_action,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Action action =list.get(position);
        holder.title.setText(action.getTitle());
        holder.minRec.setText(String.valueOf(action.getMin_Rec()));
        holder.maxRec.setText(String.valueOf(action.getMax_Rec()));
        holder.symbol.setText(action.getSymbol());

        holder.addBTN.setOnClickListener(v -> {
            //go to add food /exc activity activity
            Intent intent;
            switch (action.getTag()){
                case "Add Breakfast":
                    intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("Title",context.getString(R.string.breakfast)); //pass the title for the ui
                    intent.putExtra("Tag","Breakfast"); // pass a tag of the database
                    break;
                case "Add Lunch":
                    intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("Title",context.getString(R.string.lunch));
                    intent.putExtra("Tag","Lunch");

                    break;
                case "Add Dinner":
                    intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("Title",context.getString(R.string.dinner));
                    intent.putExtra("Tag","Dinner");

                    break;
                case "Add Snacks":
                    intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("Title",context.getString(R.string.snacks));
                    intent.putExtra("Tag","Snacks");

                    break;
                case "Add Exercise":
                    intent = new Intent(context, AddExcActivity.class);
                    intent.putExtra("Title",context.getString(R.string.exercise));
                    intent.putExtra("Tag","Exercise");
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + action.getTag());
            }
            intent.putExtra("Date",date);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,minRec,maxRec,symbol;
        ImageView actionPic, addBTN;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_action_TXT);
            minRec = itemView.findViewById(R.id.min_action_diary_TXT);
            maxRec = itemView.findViewById(R.id.max_action_diary_TXT);
            symbol = itemView.findViewById(R.id.symbol_action_TXT);
            actionPic = itemView.findViewById(R.id.action_pic_diary_PIC);
            addBTN = itemView.findViewById(R.id.add_action_diary_BTN);
        }
    }
}

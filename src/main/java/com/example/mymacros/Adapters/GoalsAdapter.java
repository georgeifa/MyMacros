package com.example.mymacros.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymacros.Application;
import com.example.mymacros.Domains.Goal;
import com.example.mymacros.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class GoalsAdapter extends ArrayAdapter<Goal> {

    private DatabaseReference dRef;
    private final Context context;

    private final Application myApp;

    public GoalsAdapter(Context context, ArrayList<Goal> goals) {
        super(context, 0, goals);
        this.context = context;
        myApp = ((Application) context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Goal goal = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_goals, parent, false);
        }

        //region Initialize Components
        //region Firebase
        dRef = myApp.getdRef().child("Goals").child(myApp.getmUser().getUid());
        //endregion

        //region findViews
        TextView goalTXT = convertView.findViewById(R.id.goal_goalsVH_TXT);
        ImageView goalStatus = convertView.findViewById(R.id.goal_status_goalsVH_IMG);
        ImageView deleteGoal = convertView.findViewById(R.id.delete_goal_goalsVH_BTN);
        //endregion
        //endregion


        initializeView(goalTXT,goalStatus,goal);
        onClickMethods(goalStatus,deleteGoal,goal);

        return convertView;
    }

    private void onClickMethods(ImageView goalStatus,ImageView deleteGoal,Goal goal) {
        deleteGoal.setOnClickListener(v -> dRef.child(goal.getGoalID()).removeValue().addOnSuccessListener(unused ->
                Toast.makeText(context, R.string.goal_deleted,Toast.LENGTH_SHORT).show()));

        goalStatus.setOnClickListener(v -> {
            goal.setAchieved(!goal.isAchieved());

            setStatusView(goalStatus,goal);

            dRef.child(goal.getGoalID()).child("achieved").setValue(goal.isAchieved()).addOnSuccessListener(unused -> {
                if(goal.isAchieved())
                    Toast.makeText(context, R.string.goal_achieved,Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setStatusView(ImageView goalStatus,Goal goal) {
        if(goal.isAchieved()){
            goalStatus.setImageResource(R.drawable.ic_achievement);
        }else{
            goalStatus.setImageResource(R.drawable.ic_goal);
        }
    }

    private void initializeView(TextView goalTXT,ImageView goalStatus,Goal goal) {
        //region Set text, image
        goalTXT.setText(goal.getDetails());

        setStatusView(goalStatus,goal);
        //endregion
    }

}

package com.example.mymacros.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mymacros.Adapters.ActionsAdapter;
import com.example.mymacros.Application;
import com.example.mymacros.Domains.Action;
import com.example.mymacros.Domains.Step_Calories;
import com.example.mymacros.Domains.User_Exercise;
import com.example.mymacros.Domains.User_Food;
import com.example.mymacros.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DiaryFragment extends Fragment {

    private RecyclerView actionsView;
    private ActionsAdapter adapter;
    private Action action;
    private ArrayList<Action> actions;

    private ProgressBar kcalPB,protPB,carbPB,fatPB;

    private TextView dateTXT,eatenTXT,burntTXT,calories_leftTXT,carbs_recTXT,prot_recTXT,fat_recTXT,carbTXT,fatTXT,protTXT;
    private ImageView prev,next;

    private final Calendar calendar = Calendar.getInstance();
    private Date date = calendar.getTime();

    private DatabaseReference dRef;
    private FirebaseUser mUser;

    private Context context;

    private int reccom_C;
    private int reccom_Burnt_C;
    private int reccom_prot;
    private int reccom_fat;
    private int reccom_carb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        // Inflate the layout for this fragment


        initializeComponents(view);
        onClickMethods();



        return view;
    }

    private void onClickMethods() {
        prev.setOnClickListener(v -> {
            date = new Date(date.getTime() - 86400000L);
            initializeView();
            initializePB();
        });

        next.setOnClickListener(v -> {
            date = new Date(date.getTime() + 86400000L);
            initializeView();
            initializePB();
        });

        dateTXT.setOnClickListener(v -> {
            date = calendar.getTime();
            initializeView();
            initializePB();
        });
    }

    private void initializeComponents(View view) {
        Application myApp = ((Application) requireActivity().getApplicationContext());

        context = getContext();

        prev = view.findViewById(R.id.prev_day_diary_BTN);
        next = view.findViewById(R.id.next_day_diary_BTN);
        dateTXT = view.findViewById(R.id.day_dairy_TXT);
        kcalPB = view.findViewById(R.id.calories_diary_PB);
        protPB = view.findViewById(R.id.prot_diary_PB);
        carbPB = view.findViewById(R.id.carbs_diary_PB);
        fatPB = view.findViewById(R.id.fat_diary_PB);
        eatenTXT = view.findViewById(R.id.eaten_kcals_diary_TXT);
        burntTXT = view.findViewById(R.id.burnt_kcals_diary_TXT);
        protTXT = view.findViewById(R.id.protein_taken_diary_TXT);
        carbTXT = view.findViewById(R.id.carbs_taken_diary_TXT);
        fatTXT = view.findViewById(R.id.fat_taken_diary_TXT);
        carbs_recTXT = view.findViewById(R.id.carbs_needed_diary_TXT);
        prot_recTXT = view.findViewById(R.id.protein_needed_diary_TXT);
        fat_recTXT = view.findViewById(R.id.fat_needed_diary_TXT);
        calories_leftTXT = view.findViewById(R.id.calories_diary_TXT);
        actionsView = view.findViewById(R.id.actions_diary_RVIEW);


        dRef = myApp.getdRef();
        mUser = myApp.getmUser();

        int[] recommendedValues = myApp.getReccomendedMacros("");

        reccom_C = recommendedValues[0];
        reccom_prot = recommendedValues[1];
        reccom_carb = recommendedValues[2];
        reccom_fat = recommendedValues[3];
        reccom_Burnt_C = recommendedValues[4];
        initializePB();


    }

    private void initializeView() {

        if(date.equals(calendar.getTime()))
            dateTXT.setText(R.string.today);
        else {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy",Resources.getSystem().getConfiguration().locale);
            dateTXT.setText(df.format(date));
        }

    }

    private void initializePB() {
        kcalPB.setMax(reccom_C);
        kcalPB.setMin(0);

        protPB.setMax(reccom_prot);
        protPB.setMin(0);
        fatPB.setMax(reccom_fat);
        fatPB.setMin(0);
        carbPB.setMax(reccom_carb);
        carbPB.setMin(0);

        fat_recTXT.setText(String.valueOf(reccom_fat));
        prot_recTXT.setText(String.valueOf(reccom_prot));
        carbs_recTXT.setText(String.valueOf(reccom_carb));


        DateFormat df = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);
        dRef.child("User_Record").child(mUser.getUid()).child(df.format(date)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int eatenC= 0;
                int burntC= 0;
                int prot=0;
                int fat=0;
                int carb = 0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    if(Objects.equals(snap.getKey(), "Exercise")){
                        for(DataSnapshot s:snap.getChildren()){
                            if(Objects.equals(s.getKey(), "Steps")){
                                Step_Calories s_c = s.getValue(Step_Calories.class);
                                assert s_c != null;
                                burntC += s_c.getCalories();
                            }else{
                                User_Exercise uE = s.getValue(User_Exercise.class);
                                assert uE != null;
                                burntC += ((uE.getReps() * uE.getSets())*uE.getExe().getCalories_per_10_reps())/10;
                            }
                        }
                    }else{
                        for(DataSnapshot s:snap.getChildren()){
                            User_Food uF = s.getValue(User_Food.class);
                            assert uF != null;
                            eatenC += (uF.getAmount() * uF.getFood().getCalories_per_100g())/100;

                            prot+= (uF.getAmount() * uF.getFood().getProt_per_100g())/100;
                            carb+= (uF.getAmount() * uF.getFood().getCarbs_per_100g())/100;
                            fat+= (uF.getAmount() * uF.getFood().getFat_per_100g())/100;
                        }
                    }
                }

                calories_leftTXT.setText(String.valueOf(reccom_C - eatenC));

                eatenTXT.setText(String.valueOf(eatenC));
                burntTXT.setText(String.valueOf(burntC));
                kcalPB.setProgress(eatenC,true);

                carbTXT.setText(String.valueOf(carb));
                carbPB.setProgress(Math.round(carb),true);

                protTXT.setText(String.valueOf(prot));
                protPB.setProgress(Math.round(prot),true);

                fatTXT.setText(String.valueOf(fat));
                fatPB.setProgress(Math.round(fat),true);


                action = new Action();

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                actionsView.setLayoutManager(linearLayoutManager);

                actions = action.getAllActions(context,reccom_C,reccom_Burnt_C);
                adapter = new ActionsAdapter(getContext(),actions,df.format(date));
                actionsView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        initializeView();
        initializePB();
    }

}
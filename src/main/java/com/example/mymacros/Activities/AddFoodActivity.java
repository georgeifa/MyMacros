package com.example.mymacros.Activities;

import static com.example.mymacros.Helpers.Constants.RecordAudioRequestCode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymacros.Adapters.AddFoodAdapter;
import com.example.mymacros.Adapters.FoodListAdapter;
import com.example.mymacros.Application;
import com.example.mymacros.Dialogs.Add_NEW_FoodDialog;
import com.example.mymacros.Domains.Calories_Stats;
import com.example.mymacros.Domains.Food;
import com.example.mymacros.Domains.User_Food;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class AddFoodActivity extends AppCompatActivity {

    //region Statements

    //region RecyclerView - Adapters - Arraylist

    private RecyclerView userList,foodList;

    private AddFoodAdapter adapter_a;
    private FoodListAdapter adapter_f;

    private ArrayList<Food> foods;
    private ArrayList<User_Food> user_foods;

    //endregion

    private TextView title,listViewEmpty,userListEmpty;
    private ImageView mic,back;
    private EditText search;

    private TextView caloriesRECDTXT, caloriesSpefRECTXT, protRECTXT, carbsRECTXT, fatRECTXT;
    private TextView caloriesDTXT, caloriesSpefTXT, protTXT, carbsTXT, fatTXT;
    private ProgressBar PB_caloriesD,PB_caloriesS,PB_prot,PB_carbs,PB_fat;

    private FloatingActionButton addFoodBTN;

    //region Firebase
    private DatabaseReference dRef;
    private FirebaseUser mUser;
    //endregion

    private Context context;

    private String date;

    private int reccom_C_spef;
    private int reccom_C;
    private int reccom_prot;
    private int reccom_fat;
    private int reccom_carb;

    private Application myApp;

    private SpeechRecognizer speechRecognizer;

    //region onCreate - onResume - onStop - onDestroy - onBackPressed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initializeComponents();
        initializeView(savedInstanceState);
        initializePB();

        onSomethingMethods();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();

        dRef.child("Stats").child(myApp.getmUser().getUid()).child("Calories").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Calories_Stats c_s = snapshot.getValue(Calories_Stats.class);

                if(c_s!=null)
                    c_s.setEaten(Integer.parseInt(caloriesDTXT.getText().toString()));
                else
                    c_s = new Calories_Stats(Integer.parseInt(caloriesDTXT.getText().toString()),0);
                dRef.child("Stats").child(myApp.getmUser().getUid()).child("Calories").child(date).setValue(c_s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    //endregion

    //region Permissions
    //check for permissions for mic
    private void checkPermission() {

        //check for permissions for mic
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //  Get the permission result
        if(requestCode==RecordAudioRequestCode && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                initializeSpeechRecognition();
            }else{
                Toast.makeText(context, R.string.permission_mic_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion

    //region Initialize Methods

    private void initializeComponents() {
        myApp = ((Application) getApplicationContext());


        //region findviews
        title = findViewById(R.id.title_add_food_TXT);
        mic = findViewById(R.id.mic_add_food_BTN);
        back = findViewById(R.id.back_add_food_BTN);
        search = findViewById(R.id.search_add_food_ETXT);

        userList = findViewById(R.id.add_food_RVIEW);
        foodList = findViewById(R.id.foodList_food_RVIEW);

        addFoodBTN = findViewById(R.id.add_food_food_BTN);

        listViewEmpty = findViewById(R.id.foodList_empty_addFood_TXT);
        userListEmpty = findViewById(R.id.userList_empty_addFood_TXT);

        caloriesSpefRECTXT = findViewById(R.id.max_rec_intake_add_TXT);
        caloriesRECDTXT = findViewById(R.id.max_intake_add_TXT);
        protRECTXT = findViewById(R.id.rec_protein_add_TXT);
        carbsRECTXT = findViewById(R.id.rec_carb_add_TXT);
        fatRECTXT = findViewById(R.id.rec_fat_add_TXT);

        caloriesSpefTXT = findViewById(R.id.current_rec_intake_add_TXT);
        caloriesDTXT = findViewById(R.id.current_intake_add_TXT);
        protTXT = findViewById(R.id.current_protein_add_TXT);
        carbsTXT = findViewById(R.id.current_carb_add_TXT);
        fatTXT = findViewById(R.id.current_fat_add_TXT);

        PB_carbs = findViewById(R.id.carbs_add_food_PB);
        PB_prot = findViewById(R.id.prot_add_food_PB);
        PB_fat = findViewById(R.id.fat_add_food_PB);
        PB_caloriesS = findViewById(R.id.rec_add_food_BAR);
        PB_caloriesD = findViewById(R.id.intake_add_food_BAR);
        //endregion

        //region firebase
        dRef = myApp.getdRef();
        mUser = myApp.getmUser();
        //endregion

        context = this;

        //region Arraylists

        user_foods = new ArrayList<>();
        foods = new ArrayList<>();
        //endregion

        // set up speech to text recognizer
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(context);


    }

    private void initializeView(Bundle savedInstanceState) {

        //get the type of the meal and the date
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                title.setText(null);
                date = "null";

            } else {
                title.setText(extras.getString("Title"));
                title.setTag(extras.getString("Tag"));
                date = extras.getString("Date");
            }
        } else {
            date =(String) savedInstanceState.getSerializable("Date");
            title.setText((String) savedInstanceState.getSerializable("Title"));
            title.setTag( savedInstanceState.getSerializable("Tag"));
        }

        //region Recommended Values

        int[] recommendedValues = myApp.getReccomendedMacros(title.getTag().toString());

        reccom_C = recommendedValues[0];
        reccom_prot = recommendedValues[1];
        reccom_carb = recommendedValues[2];
        reccom_fat = recommendedValues[3];
        reccom_C_spef = recommendedValues[4];
        //endregion

        getFoodList();

        getUserList();

    }

    private void initializePB() {

        //region Initialize ProgressBar Values

        caloriesRECDTXT.setText(String.valueOf(reccom_C));
        caloriesSpefRECTXT.setText(String.valueOf(reccom_C_spef));
        fatRECTXT.setText(String.valueOf(reccom_fat));
        protRECTXT.setText(String.valueOf(reccom_prot));
        carbsRECTXT.setText(String.valueOf(reccom_carb));

        PB_carbs.setMin(0);
        PB_prot.setMin(0);
        PB_fat.setMin(0);
        PB_caloriesD.setMin(0);
        PB_caloriesS.setMin(0);
        PB_carbs.setMax(reccom_carb);
        PB_prot.setMax(reccom_prot);
        PB_fat.setMax(reccom_fat);
        PB_caloriesD.setMax(reccom_C);
        PB_caloriesS.setMax(reccom_C_spef);
        //endregion


        //  Get All the foods that the user has previously added on this date
        dRef.child("User_Record").child(mUser.getUid()).child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int eatenC= 0;
                int eatenCS= 0;
                int prot=0;
                int fat=0;
                int carb = 0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    if(Objects.equals(snap.getKey(), title.getTag().toString())){
                        // Get the calories consumed on this specific meal
                        for(DataSnapshot s:snap.getChildren()){
                            User_Food uF = s.getValue(User_Food.class);
                            assert uF != null;
                            eatenCS += (uF.getAmount() * uF.getFood().getCalories_per_100g())/100;
                        }
                    }
                    if(!snap.getKey().equals("Exercise")){
                        // For every food get the macros and calories consumed
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


                //region Set the values of the ProgressBars

                caloriesDTXT.setText(String.valueOf(eatenC));
                PB_caloriesD.setProgress(eatenC,true);

                caloriesSpefTXT.setText(String.valueOf(eatenCS));
                PB_caloriesS.setProgress(eatenCS,true);

                carbsTXT.setText(String.valueOf(carb));
                PB_carbs.setProgress(Math.round(carb),true);

                protTXT.setText(String.valueOf(prot));
                PB_prot.setProgress(Math.round(prot),true);

                fatTXT.setText(String.valueOf(fat));
                PB_fat.setProgress(Math.round(fat),true);
                //endregion
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void onSomethingMethods() {

        back.setOnClickListener(v -> finish());
        /*open add new food dialog
             - on add btn pressed (inside the dialog) execute addFood (Check Add_NEW_FoodDialog)
             - get the new list
            */
        addFoodBTN.setOnClickListener(v -> {
            Add_NEW_FoodDialog cdd = new Add_NEW_FoodDialog(v.getContext());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

            cdd.yes.setOnClickListener(v1 -> {
                cdd.addFood();
                getFoodList();
            });

            cdd.no.setOnClickListener(v12 -> cdd.cancel());
        });

        //search for the foods that have the text in the search bar in their title
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if search bar is empty load all the available foods
                if(s.toString().isEmpty()){
                    getFoodList();
                }else{
                    ArrayList<Food> searchFoods = new ArrayList<>();

                    //get the food where the title contains the text written in the search bar
                    for(Food food: foods){
                        if(food.getTitle().toLowerCase(Resources.getSystem().getConfiguration().locale).contains(s.toString()))
                        {
                            searchFoods.add(food);
                        }
                    }

                    adapter_f = new FoodListAdapter(context,searchFoods,date,title.getTag().toString());
                    foodList.setAdapter(adapter_f);

                    setVisibilities_foodList();
                }
            }
        });

        mic.setOnClickListener(v -> {
            // check for permissions to user mic
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                checkPermission();
            }else{
                initializeSpeechRecognition();
            }
        });
    }

    private void initializeSpeechRecognition() {
        // set up speech to text recognizer
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(context);

        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Resources.getSystem().getConfiguration().locale);


        // start listening for speech to text
        mic.setOnClickListener(v -> {
            mic.setImageResource(R.drawable.ic_mic_talking);
            speechRecognizer.startListening(speechIntent);
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                search.setText("");
                search.setHint(R.string.ready_to_speak);
            }

            @Override
            public void onBeginningOfSpeech() {
                search.setHint(R.string.listening);
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

                search.setHint(R.string.search_food);
                mic.setImageResource(R.drawable.ic_mic_idle);

            }

            @Override
            public void onResults(Bundle results) {

                search.setHint(R.string.search_food);
                //get the results and put the to the search bar
                mic.setImageResource(R.drawable.ic_mic_idle);
                ArrayList<String> arrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                search.setText(arrayList.get(0));

                speechRecognizer.stopListening();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    //endregion

    //region Set Visibilities

    //If list is empty hide it - show message

    private void setVisibilities_foodList() {
        if(adapter_f.getItemCount()==0){
            foodList.setVisibility(View.INVISIBLE);
            listViewEmpty.setVisibility(View.VISIBLE);
        }else{
            foodList.setVisibility(View.VISIBLE);
            listViewEmpty.setVisibility(View.GONE);
        }
    }

    private void setVisibilities_userList() {
        if(adapter_a.getItemCount()==0){
            userList.setVisibility(View.INVISIBLE);
            userListEmpty.setVisibility(View.VISIBLE);
        }else{
            userList.setVisibility(View.VISIBLE);
            userListEmpty.setVisibility(View.GONE);
        }
    }
    //endregion

    //region Get Lists
    private void getUserList() {

        // Get All the food the user has eaten
        dRef.child("User_Record").child(mUser.getUid()).child(date).child(title.getTag().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_foods.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    user_foods.add(snap.getValue(User_Food.class));
                }

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
                userList.setLayoutManager(linearLayoutManager);
                adapter_a = new AddFoodAdapter(context,user_foods,date,title.getTag().toString());

                setVisibilities_userList();

                userList.setAdapter(adapter_a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getFoodList() {

        //get all the available foods from the database
        dRef.child("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foods.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    foods.add(snap.getValue(Food.class));
                }

                //get all the user-added food stored locally

                foods.addAll(myApp.getSharedPreferencesHelper().get_FoodList());

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                foodList.setLayoutManager(linearLayoutManager);
                adapter_f = new FoodListAdapter(context,foods,date,title.getTag().toString());
                setVisibilities_foodList();

                foodList.setAdapter(adapter_f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.unable_load_food,Toast.LENGTH_SHORT).show();
                Log.e("Food List Error",error.getMessage());
            }
        });
    }
    //endregion

}
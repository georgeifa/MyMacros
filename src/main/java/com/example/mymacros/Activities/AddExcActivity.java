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

import com.example.mymacros.Adapters.AddExcAdapter;
import com.example.mymacros.Adapters.ExerciseListAdapter;
import com.example.mymacros.Application;
import com.example.mymacros.Dialogs.Add_NEW_ExcDialog;
import com.example.mymacros.Domains.Calories_Stats;
import com.example.mymacros.Domains.Exercise;
import com.example.mymacros.Domains.Step_Calories;
import com.example.mymacros.Domains.User_Exercise;
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

public class AddExcActivity extends AppCompatActivity   {

    //region Statements


    //region RecyclerView - Adapters - Arraylist
    private RecyclerView userList,excList;

    private ExerciseListAdapter adapter_l;
    private AddExcAdapter adapter_a;

    private ArrayList<Exercise> excs;
    private ArrayList<User_Exercise> user_exes;
    //endregion

    private TextView title,stepsDone,recSteps,listViewEmpty,userListEmpty,caloriesBurnt, recBurnt;
    private ImageView mic,back;
    private EditText search;

    private ProgressBar PB_burnt,PB_steps;

    private FloatingActionButton addExeBTN;

    //region Firebase
    private DatabaseReference dRef;
    private FirebaseUser mUser;
    //endregion

    private Context context;

    private String date;

    private int reccom_C_Burnt;
    private int reccom_steps;
    private int steps;


    private Application myApp;

    private SpeechRecognizer speechRecognizer;
    //endregion

    //region onCreate - onResume - onStop - onDestroy - onBackPressed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exc);

        initializeComponents();
        initializeView(savedInstanceState);
        initializePB();
        onSomethingMethods();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set Language
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Add the calories burnt and save them to "Stats" child of the database
        dRef.child("Stats").child(myApp.getmUser().getUid()).child("Calories").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //  Get the calories Stats
                Calories_Stats c_s = snapshot.getValue(Calories_Stats.class);

                //  Add the burnt Calories
                if(c_s!=null)
                    c_s.setBurnt(Integer.parseInt(caloriesBurnt.getText().toString()));
                else
                    c_s = new Calories_Stats(0,Integer.parseInt(caloriesBurnt.getText().toString()));

                //  Save the new Calories Stats
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
        //  Destroy SpeechRecognizer if initiated
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
                //  If granted show message and initialize speech recognition
                Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show();

                initializeSpeechRecognition();
            }else{
                //  If NOT granted show message

                Toast.makeText(context, R.string.permission_mic_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion

    //region Initialization Methods - OnSomethingMethods
    private void initializeComponents() {

        myApp = ((Application) getApplicationContext());
        context = this;

        //region findViews
        title = findViewById(R.id.title_add_exc_TXT);
        mic = findViewById(R.id.mic_add_exc_BTN);
        back = findViewById(R.id.back_add_exc_BTN);
        search = findViewById(R.id.search_add_exc_ETXT);

        userList = findViewById(R.id.userList_add_exc_RVIEW);
        excList = findViewById(R.id.excList_add_exc_RVIEW);

        addExeBTN = findViewById(R.id.add_exc_exc_BTN);

        listViewEmpty = findViewById(R.id.userList_empty_addExc_TXT);
        userListEmpty = findViewById(R.id.excList_empty_addExc_TXT);

        PB_burnt = findViewById(R.id.burnt_add_exc_BAR);
        caloriesBurnt = findViewById(R.id.current_burnt_add_EXE_TXT);
        recBurnt = findViewById(R.id.max_burnt_add_Exe_TXT);

        PB_steps = findViewById(R.id.steps_add_exc_BAR);
        stepsDone = findViewById(R.id.current_steps_add_TXT);
        recSteps = findViewById(R.id.max_steps_add_TXT);
        //endregion

        //region Firebase
        dRef = myApp.getdRef();
        mUser = myApp.getmUser();
        //endregion

        //region Arraylists
        user_exes = new ArrayList<>();
        excs = new ArrayList<>();
        //endregion

        //region Recommended Values
        reccom_C_Burnt = myApp.getReccomendedMacros("")[4];
        reccom_steps = 7500;
        //endregion
    }

    private void initializeView(Bundle savedInstanceState) {

        //region  Get title, tag and date
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                title.setText(null);
                date = "null";
            } else {
                title.setText(extras.getString("Title"));
                date = extras.getString("Date");
                title.setTag(extras.getString("Tag"));
            }
        } else {
            title.setText((String) savedInstanceState.getSerializable("Title"));
            title.setTag(savedInstanceState.getSerializable("Tag"));
            date =(String) savedInstanceState.getSerializable("Date");
        }
        //endregion

        //region Initialize Lists
        getExeList();

        getUserList();
        //endregion
    }

    private void initializePB() {

        //region Initialize ProgressBar Values
        recBurnt.setText(String.valueOf(reccom_C_Burnt));
        recSteps.setText(String.valueOf(reccom_steps));

        PB_burnt.setMin(0);
        PB_burnt.setMax(reccom_C_Burnt);

        PB_steps.setMin(0);
        PB_steps.setMax(reccom_steps);
        //endregion

        steps = 0;

        //  Get All the exercises that the user has previously added on this date
        dRef.child("User_Record").child(mUser.getUid()).child(date).child(title.getTag().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int burntC= 0;
                for(DataSnapshot s:snapshot.getChildren()){
                    //if exercise is steps
                    if(Objects.equals(s.getKey(), "Steps")){

                        //get the calories burnt
                        //get the amount of steps

                        Step_Calories s_c = s.getValue(Step_Calories.class);
                        assert s_c != null;
                        burntC += s_c.getCalories();

                        steps = s_c.getSteps();
                    }
                    //if exercise is steps
                    else
                    {

                        //get the calories burnt

                        User_Exercise uE = s.getValue(User_Exercise.class);
                        assert uE != null;
                        burntC += ((uE.getReps() * uE.getSets())*uE.getExe().getCalories_per_10_reps())/10;
                    }
                }

                //region Set the values of the ProgressBars
                stepsDone.setText(String.valueOf(steps));
                PB_steps.setProgress(steps,true);
                caloriesBurnt.setText(String.valueOf(burntC));
                PB_burnt.setProgress(burntC,true);
                //endregion
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //endregion
    }

    private void onSomethingMethods() {

        back.setOnClickListener(v -> finish());

        addExeBTN.setOnClickListener(v -> {

            /*open add new exercise dialog
                - on add btn pressed (inside the dialog) execute addExercise (Check Add_NEW_ExcDialog)
                - get the new list
            */

            Add_NEW_ExcDialog cdd = new Add_NEW_ExcDialog(v.getContext());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

            cdd.add.setOnClickListener(v1 -> {
                cdd.addExercise();
                getExeList();
            });

            cdd.cancel.setOnClickListener(v12 -> cdd.cancel());
        });

        //search for the exercise that have the text in the search bar in their title
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if search bar is empty load all the available exercises
                if(s.toString().isEmpty()){
                    getExeList();
                }else{
                    ArrayList<Exercise> searchExes = new ArrayList<>();

                    //get the exercise where the title contains the text written in the search bar
                    for(Exercise exe: excs){
                        if(exe.getTitle().toLowerCase(Resources.getSystem().getConfiguration().locale).contains(s.toString()))
                        {
                            searchExes.add(exe);
                        }
                    }

                    adapter_l = new ExerciseListAdapter(context,searchExes,date,title.getTag().toString());
                    excList.setAdapter(adapter_l);

                    setVisibilities_ExeList();
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

                search.setHint(R.string.search_exc);
                mic.setImageResource(R.drawable.ic_mic_idle);

            }

            @Override
            public void onResults(Bundle results) {

                search.setHint(R.string.search_exc);
                //get the results and put the to the search bar
                ArrayList<String> arrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                search.setText(arrayList.get(0));

                mic.setImageResource(R.drawable.ic_mic_idle);
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

    private void setVisibilities_ExeList() {
        if(adapter_l.getItemCount()==0){
            excList.setVisibility(View.INVISIBLE);
            listViewEmpty.setVisibility(View.VISIBLE);
        }else{
            excList.setVisibility(View.VISIBLE);
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
        // Get All the exercises the user has done
        dRef.child("User_Record").child(mUser.getUid()).child(date).child(title.getTag().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_exes.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    //if the exercise is not steps added to the list
                    if(!Objects.equals(snap.getKey(), "Steps"))
                        user_exes.add(snap.getValue(User_Exercise.class));
                }

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
                userList.setLayoutManager(linearLayoutManager);
                adapter_a = new AddExcAdapter(context,user_exes,date,title.getTag().toString());

                setVisibilities_userList();

                userList.setAdapter(adapter_a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExeList() {
        //get all the available exercises from the database
        dRef.child("Exercises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excs.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    for(DataSnapshot s:snap.getChildren())
                        excs.add(s.getValue(Exercise.class));
                }

                //get all the user-added exercise stored locally
                excs.addAll(myApp.getSharedPreferencesHelper().get_ExeList());

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                excList.setLayoutManager(linearLayoutManager);
                adapter_l = new ExerciseListAdapter(context,excs,date,title.getTag().toString());
                setVisibilities_ExeList();

                excList.setAdapter(adapter_l);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.unable_load_exe_list,Toast.LENGTH_SHORT).show();
                Log.e("Exercise List Error",error.getMessage());
            }
        });

    }

    //endregion

}
package com.example.mymacros.Fragments;

import static com.example.mymacros.Helpers.Constants.RecordAudioRequestCode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymacros.Adapters.GoalsAdapter;
import com.example.mymacros.Application;
import com.example.mymacros.Dialogs.AddGoalDialog;
import com.example.mymacros.Domains.Goal;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class GoalsFragment extends Fragment {

    private EditText search;
    private ImageView mic,addBTN;
    private TextView gAchieved_TXT,total_g_TXT,noGoals_TXT;
    private ProgressBar achievements_bar;
    private ListView listView;
    private GoalsAdapter adapter;
    private Context context;
    private ArrayList<Goal> goals;

    private DatabaseReference dRef;

    private SpeechRecognizer speechRecognizer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        initializeComponents(view);
        getGoals();
        onSomethingMethods();
        return view;
    }

    private void onSomethingMethods() {
        addBTN.setOnClickListener(v -> {
            AddGoalDialog cdd = new AddGoalDialog(context);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });

        //search for the foods that have the text in the search bar in their title (case sensitive)
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
                    getGoals();
                }else{
                    ArrayList<Goal> searchGoals = new ArrayList<>();

                    for(Goal g: goals){
                        if(g.getDetails().toLowerCase(Resources.getSystem().getConfiguration().locale).contains(s.toString()))
                        {
                            searchGoals.add(g);
                        }
                    }

                    adapter = new GoalsAdapter(context,searchGoals);
                    listView.setAdapter(adapter);

                    initializeListView();
                    setVisibility(searchGoals);

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

    private void initializeComponents(View view) {
        Application myApp = ((Application) requireActivity().getApplicationContext());

        //region Findviews
        search = view.findViewById(R.id.search_goals_ETXT);
        mic = view.findViewById(R.id.mic_goals_BTN);
        gAchieved_TXT = view.findViewById(R.id.goals_achieved_goals_TXT);
        total_g_TXT = view.findViewById(R.id.total_goals_TXT);
        achievements_bar = view.findViewById(R.id.achievements_goals_BAR);
        listView = view.findViewById(R.id.goals_goals_LVIEW);
        addBTN = view.findViewById(R.id.add_goal_goals_BTN);
        noGoals_TXT = view.findViewById(R.id.no_goals_goals_TXT);
        //endregion

        //region Firebase
        dRef = myApp.getdRef().child("Goals").child(myApp.getmUser().getUid());
        //endregion

        // set up speech to text recognizer
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(context);

        goals = new ArrayList<>();
    }

    private void getGoals(){

        //region Goals - Achievements Listview

        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goals.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    Goal g = snap.getValue(Goal.class);
                    Objects.requireNonNull(g).setGoalID(snap.getKey());
                    goals.add(g);
                }

                adapter = new GoalsAdapter(context,goals);

                listView.setAdapter(adapter);

                initializeListView();
                setVisibility(goals);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //endregion

    }

    private void initializeListView(){

        //region Completed Progress Bar - Progress Bar Texts
        //region Progress Bar
        int goalsAchieved_num = 0;
        for(Goal g:goals){
            if(g.isAchieved()){
                goalsAchieved_num++;
            }
        }
        achievements_bar.setMax(goals.size());
        achievements_bar.setProgress(goalsAchieved_num);
        //endregion

        //region Texts
        gAchieved_TXT.setText(String.valueOf(goalsAchieved_num));
        total_g_TXT.setText(String.valueOf(goals.size()));
        //endregion
        //endregion
    }

    private void setVisibility(ArrayList<Goal> g_List) {
        if(g_List.size()==0) {
            listView.setVisibility(View.GONE);
            noGoals_TXT.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            noGoals_TXT.setVisibility(View.GONE);
        }
    }

    //check for permissions for mic
    private void checkPermission() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{
                    Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==RecordAudioRequestCode && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                initializeSpeechRecognition();
            }else{
                Toast.makeText(context, R.string.permission_mic_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeSpeechRecognition() {
        // set up speech to text recognizer
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(context);

        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


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

                search.setHint(R.string.search_goals);
                mic.setImageResource(R.drawable.ic_mic_idle);

            }

            @Override
            public void onResults(Bundle results) {

                search.setHint(R.string.search_goals);
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
}
package com.example.mymacros.Fragments;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.mymacros.Helpers.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_n_COARSE_LOCATION_n_Accelormeter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mymacros.Activities.MainActivity;
import com.example.mymacros.Application;
import com.example.mymacros.Domains.Step_Calories;
import com.example.mymacros.Domains.UserProfile;
import com.example.mymacros.R;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment extends Fragment  implements OnMapReadyCallback, SensorEventListener, LocationListener {

    private GoogleMap mMap;
    private Context context;
    private TextView startBTN,distance,timeTXT,steps,calories,pace;
    private DatabaseReference dRef;
    private FirebaseUser mUser;
    private UserProfile userP;

    private Application myApp;

    private LocationManager locationManager;

    private List<ArrayList<LatLng>> listOfRoutes;
    private boolean newRoute=false;

    private boolean start,firstStart = false;
    private float steps1 = 0;

    boolean run = false;

    private SensorManager sensorManager;

    private View view;

    private Location prevLoc;
    private float dist =0;

    private boolean granted;

    private Timer timer;
    private int time;
    private TimerTask timerTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        context = getContext();

        assert context != null;
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            granted = false;
            checkPermission();
        }else
            granted=true;

        if(granted){
            initializeComponents();
            initializeView();

        }
        return view;
    }


    //region Check For Permission and if GPS is ENABLED
    //check for permissions for mic
    private void checkPermission() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSIONS_REQUEST_ACCESS_FINE_n_COARSE_LOCATION_n_Accelormeter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode== PERMISSIONS_REQUEST_ACCESS_FINE_n_COARSE_LOCATION_n_Accelormeter && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //restart view if permission is granted to load everything correctly

                FragmentTransaction tr = requireActivity().getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.fragment_container, new MapFragment());
                tr.commit();
            }else{
                Toast.makeText(context, R.string.this_page_require_gps, Toast.LENGTH_SHORT).show();

                ((MainActivity) requireActivity()).go_back_to_diary();
            }
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.gps_seem_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) ->
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    Toast.makeText(context, R.string.this_page_require_gps, Toast.LENGTH_SHORT).show();

                    ((MainActivity) requireActivity()).go_back_to_diary();
                    dialog.dismiss();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);

        //restart view if permission is granted to load everything correctly

        FragmentTransaction tr = requireActivity().getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, new MapFragment());
        tr.commit();
    }

    //endregion


    private void initializeComponents() {
        myApp = ((Application) requireActivity().getApplicationContext());

        //region Firebase

        mUser = myApp.getmUser();
        dRef = myApp.getdRef();

        //endregion

        //region findViews

        startBTN = view.findViewById(R.id.start_map_BTN);
        distance = view.findViewById(R.id.distance_map_TXT);
        pace = view.findViewById(R.id.pace_map_TXT);
        timeTXT = view.findViewById(R.id.time_map_TXT);
        calories = view.findViewById(R.id.calories_map_TXT);
        steps = view.findViewById(R.id.steps_map_TXT);

        //endregion

        //region PolylineOptions

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.WHITE);
        polylineOptions.width(25f);

        //endregion

        //region Map Initialization

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_map_MFRAG);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        listOfRoutes = new ArrayList<>();
        //endregion

        sensorManager = (SensorManager) this.context.getSystemService(SENSOR_SERVICE);

        timer = new Timer();

    }

    private void initializeView() {
        onSomethingMethods();

        Toast.makeText(context, R.string.make_sure_good_gps_signal,Toast.LENGTH_SHORT).show();
    }


    private void onSomethingMethods() {

        startBTN.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                checkPermission();
            }else{
                if(startBTN.getText().toString().equals(getResources().getString(R.string.start))){
                    startClick();
                    startBTN.setText(R.string.stop);
                    startBTN.setBackgroundResource(R.drawable.btn_idle_bg_red_style);

                }else{
                    stopClick();
                    startBTN.setText(getResources().getString(R.string.start));
                    startBTN.setBackgroundResource(R.drawable.btn_add_bg);

                }
            }

        });
    }

    private void startClick(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }else{
            newRoute=true;

            firstStart = true;
            start = true;
            
            startTimer();
        }
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    time++;
                    timeTXT.setText(getTimeText());
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask,0,1000);
    }


    int seconds = 0;
    int minutes = 0;
    private String getTimeText() {

        seconds = time % 60;
        minutes = time/60;

        return formatTime(minutes,seconds);
    }

    private String formatTime(int minutes, int seconds) {
        return (minutes < 10 ? "0" : "") +minutes + " : " + String.format(Resources.getSystem().getConfiguration().locale,"%02d",seconds);
    }


    private void stopClick() {

        start = false;
        run = false;
        timerTask.cancel();
    }

    @Override
    public void onStop() {
        locationManager.removeUpdates(this);
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);
        dRef.child("User_Record").child(mUser.getUid()).child(df.format(Calendar.getInstance().getTime())).child("Exercise").child("Steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Step_Calories s_c = snapshot.getValue(Step_Calories.class);
                if(s_c != null){
                    s_c.setSteps(s_c.getSteps()+Integer.parseInt(steps.getText().toString()));
                    s_c.setCalories(s_c.getCalories()+Math.round(Float.parseFloat(calories.getText().toString())));
                }else{
                    s_c = new Step_Calories();
                    s_c.setSteps(Integer.parseInt(steps.getText().toString()));
                    s_c.setCalories(Math.round(Float.parseFloat(calories.getText().toString())));

                }
                dRef.child("User_Record").child(mUser.getUid()).child(df.format(Calendar.getInstance().getTime())).child("Exercise").child("Steps").setValue(s_c);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStop();

    }

    //region Map Methods

    //region When Map Is Ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        //region Map Styling
        mMap = googleMap;
        mMap.setMinZoomPreference(15.0f);
        mMap.setBuildingsEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context,R.raw.map_styling));
        //endregion

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }else {

            //region Get User Current Location
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            //endregion
        }


    }
    //endregion

    //region Draw the Route the user has made
    private void addLocationToRoute(Location location,boolean newRoute) {
        mMap.clear();

        ArrayList<LatLng> LatLngs;
        if(newRoute){
            LatLngs = new ArrayList<>();
            LatLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
            listOfRoutes.add(LatLngs);
            this.newRoute = false;
        }else{
            LatLngs = listOfRoutes.get(listOfRoutes.size()-1);
            LatLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
        }

        for(ArrayList<LatLng> L:listOfRoutes)
        {
            PolylineOptions op = new PolylineOptions();
            op.color(Color.WHITE);
            op.width(10f);
            mMap.addPolyline(op.addAll(L));
        }
    }
    //endregion


    @Override
    public void onSensorChanged(SensorEvent event) {


        if(firstStart){
            steps1 = event.values[0];
            firstStart = false;
        }

        if(start){

            steps.setText(String.valueOf((int)Math.round(event.values[0] - steps1)));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(granted)
            stopClick();
    }


    @Override
    public void onResume() {
        super.onResume();

        if(granted) {

            run = true;
            Sensor count = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if (count != null) {

                sensorManager.registerListener(this, count, sensorManager.SENSOR_DELAY_UI);

            } else {

                //Toast.makeText(this, "  ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private int total_paces = 0;
    private int paces = 0;
    private int average;
    private int caloriesBurnt=0;

    private int caloriesBurn(int average){

        if(average>2){

            if(average<=3){

                caloriesBurnt = Math.round(2 * totalSeconds/30);

            }else if(average<=4){

                caloriesBurnt = Math.round(4 * totalSeconds/30);

            }else if(average<=5){

                caloriesBurnt = Math.round(5 * totalSeconds/30);

            }else if(average<=6){

                caloriesBurnt = Math.round(7 * totalSeconds/30);

            }else if(average<=7){

                caloriesBurnt = Math.round(9 * totalSeconds/30);

            }else if(average<=8){

                caloriesBurnt = Math.round(10 * totalSeconds/30);

            }else if(average<=9){

                caloriesBurnt = Math.round(12 * totalSeconds/30);

            }else if(average<=10){

                caloriesBurnt = Math.round(14 * totalSeconds/30);

            }else if(average<=11){

                caloriesBurnt = Math.round(16 * totalSeconds/30);

            }else if(average<=12){

                caloriesBurnt = Math.round(18 * totalSeconds/30);

            }else{

                caloriesBurnt = Math.round(20 * totalSeconds/30);

            }
        }


        return caloriesBurnt;
    }


    private int totalSeconds;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
        if(start) {
            addLocationToRoute(location, newRoute);
            pace.setText(String.format(Resources.getSystem().getConfiguration().locale,"%.2f",location.getSpeed() *3.6)); //speed in km/h


            //  m/s to mph
            int mph = (int) Math.round(location.getSpeed() * 2.236936);

            //don't count less than 2mph
            if(mph >2){

                //keep total counted paces
                total_paces ++;

                //summary of the total paces(mph)
                paces = paces + mph;

                //get average pace(mph)
                average = paces/total_paces;


                //get total seconds
                totalSeconds = minutes*60 + seconds;

                //showing burnt calories after 30 seconds
                if(totalSeconds>30){
                    calories.setText(String.valueOf(caloriesBurn(average)));
                }
            }



            dist += prevLoc.distanceTo(location) / 1000;
            distance.setText(String.format(Resources.getSystem().getConfiguration().locale,"%.2f",dist));
        }
        prevLoc = location;
    }


    //endregion





}

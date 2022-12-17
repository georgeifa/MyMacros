package com.example.mymacros.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mymacros.Application;
import com.example.mymacros.Fragments.AccountFragment;
import com.example.mymacros.Fragments.DiaryFragment;
import com.example.mymacros.Fragments.GoalsFragment;
import com.example.mymacros.Fragments.MapFragment;
import com.example.mymacros.Fragments.StatsFragment;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    private int activeFragment;
    private BottomNavigationView bottomNavigationView;
    private Application myApp;

    //region onCreate - onResume- onBackPressed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = ((Application) getApplicationContext());

        checkIfLoggedIn();

        if(checkIfLoggedIn())
            initializeComponents_View();

    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleHelper.setLanguage(this,myApp.getmUser().getUid());
    }

    //when press back don't go to login activity
    // if presses 2 times in 3 secs exit app
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if(activeFragment != R.id.diary){
            bottomNavigationView.setSelectedItemId(R.id.diary);
        }else{
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, R.string.press_back_again_to_exit,
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(() -> exit = false, 3 * 1000);

            }
        }
    }
    //endregion

    private void initializeComponents_View() {


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        if(fragment == null){
            //if this the first time opening the MainActivity open the diary fragment
        bottomNavigationView.setSelectedItemId(R.id.diary);
        fragment = new DiaryFragment();
        activeFragment = R.id.diary;
        }
        else{
            //otherwise the account fragment
            bottomNavigationView.setSelectedItemId(R.id.account);
            fragment = new AccountFragment();
            activeFragment = R.id.account;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }


    //bottom navigation functionality
    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod= item -> {

        fragment = null;
        activeFragment = item.getItemId();
        switch (item.getItemId()){
            case  R.id.diary:
                fragment = new DiaryFragment();
                break;
            case  R.id.map:
                fragment = new MapFragment();
                break;
            case  R.id.account:
                fragment = new AccountFragment();
                break;
            case  R.id.goals:
                fragment = new GoalsFragment();
                break;
            case  R.id.stats:
                fragment = new StatsFragment();
                break;
        }

        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        return true;
    };

    //if user is not logged in go back to login page
    private boolean checkIfLoggedIn() {
        if (myApp.getmUser() == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return false;
        }
        return true;
    }

    //go back to diary
    public void go_back_to_diary(){
        bottomNavigationView.setSelectedItemId(R.id.diary);
        fragment = new DiaryFragment();
        activeFragment = R.id.diary;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

    }

}
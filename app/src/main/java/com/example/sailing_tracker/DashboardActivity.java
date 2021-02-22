package com.example.sailing_tracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    // Firebase auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Instantiate the firebase auth class
        firebaseAuth = FirebaseAuth.getInstance();

        // Instantiate the toolbar
        final Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        // Set default toolbar value
        mToolBar.setTitle("Home");

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // If the home icon is clicked then change the fragment to the home fragment view
                    case R.id.nav_home:
                        mToolBar.setTitle("Home");
                        HomeFragment homeFragment = new HomeFragment();
                        homeFragment.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, homeFragment).commit();
                        break;

                    case R.id.nav_record:
                        // If the record icon is clicked then change the fragment to the record fragment view
                        mToolBar.setTitle("Record");
                        RecordFragment recordFragment = new RecordFragment();
                        recordFragment.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, recordFragment).commit();
                        break;
                    case R.id.nav_profile:
                        // If the profile icon is clicked then change the fragment to the profile fragment view
                        mToolBar.setTitle("Profile");
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileFragment.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, profileFragment).commit();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_signOut) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers){
           // To be implemented
            Intent myIntent = new Intent(DashboardActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return true;
    }

    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in stay here
            // Set email of logged in user
        }
        else{
            // User is not signed in
            startActivity(new Intent(DashboardActivity.this, SignInActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
}



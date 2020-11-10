package com.example.sailing_tracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

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


        // Init
        firebaseAuth = FirebaseAuth.getInstance();


        // Toolbar init
        final Toolbar mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        // Set default toolbar value
        mTopToolbar.setTitle("Home");





        // TODO: 09/11/2020 Add navigation between fragments to bottom nav
        // Bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.nav_home:
                       mTopToolbar.setTitle("Home");
                       HomeFragment fragment1 = new HomeFragment();
                       fragment1.setArguments(getIntent().getExtras());
                       getSupportFragmentManager().beginTransaction()
                               .add(R.id.fragment_container, fragment1).commit();
                       break;
                   case R.id.nav_profile:
                       mTopToolbar.setTitle("Profile");
                       ProfileFragment fragment2 = new ProfileFragment();
                       fragment2.setArguments(getIntent().getExtras());
                       getSupportFragmentManager().beginTransaction()
                               .add(R.id.fragment_container, fragment2).commit();
                       break;
                   case R.id.nav_users:
                       mTopToolbar.setTitle("Users");
                       ProfileFragment fragment3 = new ProfileFragment();
                       fragment3.setArguments(getIntent().getExtras());
                       getSupportFragmentManager().beginTransaction()
                               .add(R.id.fragment_container, fragment3).commit();

                       break;

               }
            return true;

            }

        });




        // Bottom navigation;
        // TODO: 05/11/2020 Navigation for menu needed to be coded
        // TODO: 05/11/2020 Fragments need to be edited - YOUTUBE

    }






/*
    BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()

    {
        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item){
        // Handle item clicks
        if (item.getItemId() == R.id.nav_home) { // Home fragment transaction
            mTopToolbar.setTitle("Home"); // Change actionbar title
            HomeFragment fragment1 = new HomeFragment();
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.content, fragment1, "");
            ft1.commit();
            return true;
        }
        if (item.getItemId() == R.id.nav_profile) { // Profile fragment transaction
            mTopToolbar.setTitle("Profile"); // Change actionbar title
            ProfileFragment fragment2 = new ProfileFragment();
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.content, fragment2, "");
            ft2.commit();
            return true;
        }
        if (item.getItemId() == R.id.nav_users) { // Users fragment transaction
            mTopToolbar.setTitle("Users"); // Change actionbar title
            UsersFragment fragment3 = new UsersFragment();
            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
            ft3.replace(R.id.content, fragment3, "");
            ft3.commit();
            return true;
        }
        return false;
    }
    });
}

 */





















    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_favorite:
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            // User is signed in stay here
            // Set email of logged in user
            //
        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void setSupportActionBar(Toolbar myToolbar) {
        getSupportActionBar();
    }






}


package com.example.sailing_tracker;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class DashboardActivity extends AppCompatActivity {

    // Firebase auth
    FirebaseAuth firebaseAuth;

    // Views init
    TextView mProfileTv;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Find the toolbar view inside the activity layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);







        // Init
        firebaseAuth = FirebaseAuth.getInstance();

        // Init views
        mProfileTv = findViewById(R.id.profileTv);

        // Bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemReselectedListener((BottomNavigationView.OnNavigationItemReselectedListener) selectedListener);

        //actionBar.setTitle("Home"); // Default fragment
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();






    }


    /* private void setSupportActionBar(Toolbar toolbar) {
        AppCompatActivity.getSupportActionBar(toolbar);
    }

     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Nullable
    @Override
    public ActionBar getActionBar() {
        return super.getActionBar();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Handle item clicks
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            // Home fragment transaction
                            ; // Change actionbar title
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return  true;
                        default:
                            throw new IllegalStateException("Unexpected value: " + item.getItemId());
                    }
                    switch (item.getItemId()){
                        case R.id.nav_profile:
                            // Profile fragment transaction
                            // actionBar.setTitle("Profile"); // Change actionbar title
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return  true;
                    }
                    switch (item.getItemId()){
                        case R.id.nav_users:
                            // Users fragment transaction
                           // actionBar.setTitle("Users"); // Change actionbar title
                            UsersFragment fragment3= new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            return  true;
                    }
                    return false;
                }
            };

    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            // User is signed in stay here
            // Set email of logged in user
            //
        }
    }


}
//TODO:
// Replace actionbar with tool bar
// Update methods using action bar
// Test profile page
// Fix sign in with google
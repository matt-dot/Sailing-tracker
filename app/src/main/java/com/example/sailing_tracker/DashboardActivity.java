package com.example.sailing_tracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class

DashboardActivity extends AppCompatActivity {

    // Firebase auth
    FirebaseAuth firebaseAuth;
    List<ModelUser> userList;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        // Init
        firebaseAuth = FirebaseAuth.getInstance();


        // Toolbar init
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
                    case R.id.nav_home:
                        mToolBar.setTitle("Home");
                        HomeFragment fragment1 = new HomeFragment();
                        fragment1.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment1).commit();
                        break;

                    case R.id.nav_record:
                        mToolBar.setTitle("Record");
                        RecordFragment fragment2 = new RecordFragment();
                        fragment2.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment2).commit();
                        break;
                    case R.id.nav_profile:
                        mToolBar.setTitle("Profile");
                        ProfileFragment fragment3 = new ProfileFragment();
                        fragment3.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment3).commit();
                        break;

                }
                return true;

            }

        });


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
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        }
    }



    private void searchUsers(final String query) {
        // Get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get path of database named users
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // Get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    // Get all searched users except one currently signed in
                    if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase())
                                || modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }

                    }


                    AdapterUsers mAdapter = new AdapterUsers(DashboardActivity.this, userList);
                    // Refresh adapter
                    mAdapter.notifyDataSetChanged();
                    // Set adapter to recycler view
                    recyclerView.setAdapter(mAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllUsers() {
        // Get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get path of database named users
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // Get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    // Get all users except one currently signed in
                    if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                        userList.add(modelUser);
                    }


                    AdapterUsers mAdapter = new AdapterUsers(DashboardActivity.this, userList);
                    recyclerView.setAdapter(mAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers){
            Intent myIntent = new Intent(DashboardActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);


        return true;

    }
}

// TODO: 03/12/20 UI of record

package com.example.sailing_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity{

    // Init of variables
    private List<ModelUser> userList;
    private RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;

    public UsersActivity(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Toolbar init
        // The toolbar is instantiated and assigned a title
        final Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("Users");

        // RecyclerView
        recyclerView = findViewById(R.id.users_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Init the array list to storing the list of users
        userList = new ArrayList<>();

        // Firebase init
        firebaseAuth = FirebaseAuth.getInstance();

        // Method call to populate userList
        getAllUsers();

        }

    public void getAllUsers () {
            // Get current user
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // Get path of database named users
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            // Get all data from path
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Clear user list before populating
                    userList.clear();
                    // Iterate through the firebase database and assign each iteration to ModelUser class
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        // Get all users except one currently signed in
                        if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                            userList.add(modelUser);
                        }
                        AdapterUsers mAdapter = new AdapterUsers(userList);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UsersActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(UsersActivity.this, MainActivity.class));

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers){
            Intent myIntent = new Intent(UsersActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}







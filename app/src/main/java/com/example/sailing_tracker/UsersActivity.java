package com.example.sailing_tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

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
    Context context;
    private List<ModelUser> userList;
    private AdapterUsers adapterUsers;
    private RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;

    public UsersActivity(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Toolbar
        final Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("Users");

        // RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.users_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterUsers = new AdapterUsers(userList);
        userList = new ArrayList<>();




        // Firebase init
        firebaseAuth = firebaseAuth.getInstance();
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
                    userList.clear();
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterUsers.getFilter().filter(newText);
                return false;
            }
        });

        return true;

    }

    }







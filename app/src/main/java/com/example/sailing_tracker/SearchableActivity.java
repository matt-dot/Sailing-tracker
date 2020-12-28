package com.example.sailing_tracker;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class SearchableActivity extends Activity {
    List<ModelUser> userList;
    RecyclerView recyclerView;
    String query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.search_recyclerView);

        userList = new ArrayList<>();
       // getAllUsers();
        handleIntent(getIntent());

    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
             query = intent.getStringExtra(SearchManager.QUERY);
        }
        searchUsers(query);
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


                    AdapterUsers mAdapter = new AdapterUsers(SearchableActivity.this, userList);
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


                    AdapterUsers mAdapter = new AdapterUsers(SearchableActivity.this, userList);
                    recyclerView.setAdapter(mAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}

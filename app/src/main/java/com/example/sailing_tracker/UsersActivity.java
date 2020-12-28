package com.example.sailing_tracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

    private RecyclerView recyclerView;
    private List<ModelUser> userList;
    // private AdapterUsers adapterUsers;

    FirebaseAuth firebaseAuth;



    public UsersActivity(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userList = new ArrayList<>();
        setContentView(R.layout.activity_users);
        recyclerView = (RecyclerView) findViewById(R.id.users_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();

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


                        AdapterUsers mAdapter = new AdapterUsers(UsersActivity.this, userList );
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
}



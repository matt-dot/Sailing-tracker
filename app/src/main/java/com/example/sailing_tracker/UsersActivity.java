package com.example.sailing_tracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
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




    public UsersActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recyclerView = (RecyclerView) findViewById(R.id.users_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
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
    }



package com.example.sailing_tracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Adapters.AdapterPosts;
import com.example.sailing_tracker.Models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


// Logic error, posts being displayed by alphabetical order.
public class HomeFragment extends Fragment{

    // Firebase
    FirebaseAuth firebaseAuth;


    // Define variables
    private RecyclerView recyclerView;
    private List<ModelPost> postList;
    AdapterPosts adapterPosts;
    public static String sessionIDForPath;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();



    public HomeFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        // recycler view and its properties
        recyclerView = view.findViewById(R.id.postsRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // Shows newest post first
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        // init post list
        postList = new ArrayList<>();

        // Call the method to populate the home fragment
        loadPost();

        return view;

    }

    public void loadPost() {
        // Path of all posts
        // Get all data from this reference
        mDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        // Add the model post to postList list
                        postList.add(modelPost);
                        // Adapter
                        adapterPosts = new AdapterPosts(getActivity(), postList);
                        // Set adapter to recycler view
                        recyclerView.setAdapter(adapterPosts);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void receiveSessionID(String receivedSessionID) {
        // Assign the sessionID from RecordFragment to variable
        // This will be used for database path
        sessionIDForPath = receivedSessionID;


        // Log to make sure the session ID matches the session ID in
        // generated in record fragment
        Log.d("SessionIdCheck", "Session: " + sessionIDForPath);

    }






}
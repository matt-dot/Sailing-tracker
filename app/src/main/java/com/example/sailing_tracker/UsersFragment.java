package com.example.sailing_tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    RecyclerView.LayoutManager layoutManager;


    public UsersFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        // Init recycler view
        recyclerView = view.findViewById(R.id.users_recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();

        // Get all users
        getAllUsers();

        return view;
    };






    private void getAllUsers() {
        // Get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get path of database named users
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // Get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    // Get all users except one currently signed in

                    if(!modelUser.getUid().equals(firebaseUser.getUid())){
                        userList.add(modelUser);
                    }

                    // Adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    // Set adapter to recycler view

                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}

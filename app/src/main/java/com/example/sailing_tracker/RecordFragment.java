package com.example.sailing_tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecordFragment extends Fragment{

    // Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Views from xml
    ImageView profilePicIv;
    TextView nameTv, emailTv, phoneTv;

    public RecordFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        // Init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users" );

        // Init views
        profilePicIv = view.findViewById(R.id.profilePicIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTV);
        phoneTv = view.findViewById(R.id.phoneTv);

        /*
        Get info of currently signed in user. Using email.
        Using orderByChild query show detail from a node
        whose key named email has value equal to email currently signed in
        Search all nodes, where key matches, details will be retrieved.
         */

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check  until required data got
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // Get data
                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();
                    String phone = ""+ds.child("phone").getValue();
                    String image = ""+ds.child("image").getValue();

                    // Set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        // If image received set to profilePic image view
                        Picasso.get().load(image).into(profilePicIv);
                    }
                    catch(Exception e){
                        // Set default if exception
                        Picasso.get().load(R.drawable.ic_add_image).into(profilePicIv);


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}
package com.example.sailing_tracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    // Firebase auth
    FirebaseAuth firebaseAuth;

    // Views init
    TextView mProfileTv;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Action bar and title
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Profile");

        // Init
        firebaseAuth = FirebaseAuth.getInstance();

        // Init views
        mProfileTv = findViewById(R.id.profileTv);




    }


}
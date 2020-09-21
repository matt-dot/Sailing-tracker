package com.example.sailing_tracker;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    Button mRegisterBtn, mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initiate views

        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);

        // register button click

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Start register activity
                   startActivity(new Intent (MainActivity.this, RegisterActivity.class));



            }
        });




    }







}

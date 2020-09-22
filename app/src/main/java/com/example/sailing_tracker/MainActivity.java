package com.example.sailing_tracker;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    Button mRegisterBtn, mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init views

        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.login_btn);

        // register button click

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Start register activity
                   startActivity(new Intent (MainActivity.this, RegisterActivity.class));

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start login activity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });



    }







}

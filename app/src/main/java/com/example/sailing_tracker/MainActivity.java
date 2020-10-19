package com.example.sailing_tracker;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



public class MainActivity extends AppCompatActivity {
    // Views
    Button mRegisterBtn, mLoginBtn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Init views
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.login_btn);

        // Register button click
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}

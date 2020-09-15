package com.example.sailing_tracker;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.io.*;
import java.util.ArrayList;
import java.util.jar.Attributes;


public class MainActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private Button LoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Email = (EditText) findViewById(R.id.et_email);
        Password = (EditText) findViewById(R.id.et_password);
        LoginButton = (Button) findViewById(R.id.btn_login);

        LoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
            validate(Email.getText().toString(), Password.getText().toString());
            }

        });


    }

    private void validate(String userName, String userPassword){
        if((userName.equals("Admin")) && (userPassword.equals("1234"))){
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        } else {
            
        }
    }
}

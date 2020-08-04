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



public class MainActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private Button Login;
    private int counter = 5;

    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();
        public AuthenticationPagerAdapter (FragmentManager fm) {
            super(fm);

        }
        @Override
        public Fragment getItem(int i){
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        void addFragment(Fragment fragment){
            fragmentList.add(fragment);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);

        AuthenticationPagerAdapter pagerAdapter = new
                AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new LoginFragment());
        pagerAdapter.addFragment(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);

        Name = (EditText)findViewById(R.id.et_email);
        Password = (EditText)findViewById(R.id.et_password);
        Login = (Button)findViewById(R.id.btn_login);

        Login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                validateLogin(Name.getText().toString(), Password.getText().toString());
            }
        });



    }


    private void validateLogin(String userName, String userPassword){
    if(userName.equals("Admin") && (userPassword.equals("1234"))) {
        Intent intent = new Intent (MainActivity.this, SecondActivity.class);
        startActivity(intent);
    } else {
        counter--;
        if (counter == 0);
        Login.setEnabled(false);

        }
    }
}

package com.example.sailing_tracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.viewpager.widget.ViewPager;


public class LoginFragment extends Fragment {
    private EditText Name;
    private EditText Password;
    private Button Login;
    private int counter = 5;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentHandle = inflater.inflate(R.layout.fragment_login, container, false);


        Login = (Button) fragmentHandle.findViewById(R.id.btn_login);

        Login.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Name = (EditText) getActivity().findViewById(R.id.et_email);
                Password = (EditText) getActivity().findViewById(R.id.et_password);
                validateLogin(Name.getText().toString(), Password.getText().toString());
            }
        });
        return fragmentHandle;

    }




    private void validateLogin(String userName, String userPassword) {
        if (userName.equals("Admin") && (userPassword.equals("1234"))) {
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            startActivity(intent);
        } else {
            counter--;
            if (counter == 0) ;
            Login.setEnabled(false);

        }

    }
}










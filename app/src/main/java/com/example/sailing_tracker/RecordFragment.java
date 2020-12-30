package com.example.sailing_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RecordFragment extends Fragment {

    // Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private static final int LOCATION_REQUEST_CODE = 500;

    //

    public RecordFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLocationPermissions();
        if(Build.VERSION.SDK_INT >= 23) {
            if (!checkLocationPermissions()) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 500);
            } else {
                startService();
            }

        }
        }

    public boolean checkLocationPermissions(){
        // Check if storage permission enabled or not
        // Return true if enabled
        // Return false if disabled

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    void startService(){
        Intent intent = new Intent (getActivity(), LocationService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService();
                } else {
                    Toast.makeText(getActivity(), "Permission are required for the app to function properly", Toast.LENGTH_LONG).show();
                }
        }
    }
}
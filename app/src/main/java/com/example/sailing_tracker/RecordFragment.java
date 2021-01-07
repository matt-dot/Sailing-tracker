package com.example.sailing_tracker;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.time.StopWatch;

import static java.lang.Integer.MAX_VALUE;


public class RecordFragment extends Fragment {

    StopWatch watch;

    // Constants
    private static final int PERMISSION_FINE_LOCATION_CODE = 99;

    private static final String TAG = "RecordFragment";

    int speedInKnots;


    TextView mCurrent_speedTv, mBearingTv, mElapsedTimeTv;

    // Reference to UI element record button
    Button mButtonStartLocationUpdates, mButtonStopLocationUpdates;



    public RecordFragment(){
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        watch = new StopWatch();

        // Assign values
        mButtonStartLocationUpdates = view.findViewById(R.id.buttonRecord);
        mButtonStopLocationUpdates = view.findViewById(R.id.buttonStop);
        mCurrent_speedTv = view.findViewById(R.id.current_speedTv);
        mBearingTv = view.findViewById(R.id.bearingTv);
        mElapsedTimeTv = view.findViewById(R.id.elapsed_timeTv);

        mButtonStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Click of start location service");
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_FINE_LOCATION_CODE
                    );
                } else {
                    startLocationService();
                }
            }
        });

        mButtonStopLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Click of stop location service");
                stopLocationService();
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double latitude = intent.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0);
                        double longitude = intent.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0);
                        mCurrent_speedTv.setText("Lat: " + latitude + ", Lng: " + longitude);
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_FINE_LOCATION_CODE && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            } else {
                Toast.makeText(getActivity(), "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service :
                activityManager.getRunningServices(MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClass())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
            }
        return false;
        }


    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(ConstantsForLocationService.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(intent);
            Toast.makeText(getActivity(), "Location service started", Toast.LENGTH_SHORT).show();

        }
    }
    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(ConstantsForLocationService.ACTION_STOP_LOCATION_SERVICE);
            getActivity().stopService(intent);
            Toast.makeText(getActivity(), "Location service stopped", Toast.LENGTH_SHORT).show();

        }
    }


    }
















package com.example.sailing_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class RecordFragment extends Fragment {

    private static final String TAG = "RecordFragment";


    // Location callback
    private LocationCallback locationCallback;


    // Constants for update intervals
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;

    // Reference to UI element record button
    Button mButtonRecord;

    // Google API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    // Location request is config file for all setting related to fusedLocationProviderClient
    LocationRequest locationRequest;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        // Assign value to buttonRecord
        mButtonRecord = view.findViewById(R.id.buttonRecord);

        // Instantiate and then set all properties of LocationRequest
        locationRequest = new LocationRequest();

        // Default frequency of location check
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Frequency of update interval when set to most frequent update
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        // Setting priority of the location request.
        // i.e setting the accuracy of the location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // This event is triggered whenever the update interval is met
        // i.e. triggered every 2000ms (2 seconds)
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Save the location to a variable
                Location location = locationResult.getLastLocation();
                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLatitude());

            }
        };


        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Turn on tracking
                startLocationUpdates();
            }
        });





        updateGPS();
        // Return the view
       return view;
    } // End of onCreate()

    @SuppressLint("MissingPermission")
    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getActivity(), "Location no longer being tracked", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStop: Location is no longer being tracked");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Toast.makeText(getActivity(), "Location is being tracked", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        // Switch statement to permission requests
        switch (requestCode) {
            // If the request code is 99 (PERMISSION_FINE_LOCATION)
            case PERMISSION_FINE_LOCATION:
                // If the result of the request permission call is 0 (granted)
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All permissions are granted call the method updateGPS()
                    updateGPS();
                } else {
                    // Generate toast to inform user the app will not function
                    Toast.makeText(getActivity(), "This app requires location permission to function ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateGPS() {
        // 1. Get permissions from the user to use GPS services
        // 2. Get the current location of the user from the fusedClientProvider
        // 3. Output the update to logcat

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        // Permissions check
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Permissions are granted. Put the values of the location
                    updateLogAndToast(location);

                }
            });


        } else {
            // Permission denied
            // Ask user to grant permissions

            // Check if the build version is greater than or equal to
            // build version 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }

        }


    }

    private void updateLogAndToast(Location location) {
        if (location != null) {
            // Update the log with new location
            //Toast.makeText(getActivity(), "Latitude: " + (location.getLatitude()), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(), "Longitude:  " + (location.getLongitude()), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Latitude: " + location.getLatitude());
            Log.d(TAG, "Longitude: " + location.getLongitude());

        } else {
            Toast.makeText(getActivity(), "Location is null", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "updateLog: Location is null ");

        }

    }




}


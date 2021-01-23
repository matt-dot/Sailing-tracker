package com.example.sailing_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.time.StopWatch;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Integer.MAX_VALUE;


public class RecordFragment extends Fragment {

    // Declare the StopWatch
    StopWatch watch;

    // Constants, used in permission requests
    private static final int PERMISSION_FINE_LOCATION_CODE = 99;

    // Log tag declaration used for debugging purposes
    private static final String TAG = "RecordFragment";

    // Declare variables to be used to populate xml assets
    double speedInKnots, doubleBearing, timeInSeconds, time;

    // Declare variables to be assigned to xml elements
    TextView mCurrent_speedTv, mBearingTv, mElapsedTimeTv;
    Button mButtonStartLocationUpdates, mButtonStopLocationUpdates, mButtonReset, mButtonUpload;

    FirebaseAuth mAuth;

    String sessionID;
    LatLng latLng;


    ArrayList<LatLng> latLongArray = new ArrayList<>(); // Create an ArrayList object
    UploadSessionActivity uploadSessionActivity = new UploadSessionActivity();

    ArrayList<Float> speedData = new ArrayList<>();


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Assign view to the inflated xml layout
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mAuth = FirebaseAuth.getInstance();


        // Instantiate the stopwatch class
        watch = new StopWatch();

        // Assign values
        mButtonStartLocationUpdates = view.findViewById(R.id.buttonRecord);
        mButtonStopLocationUpdates = view.findViewById(R.id.buttonStop);
        mCurrent_speedTv = view.findViewById(R.id.current_speedTv);
        mBearingTv = view.findViewById(R.id.bearingTv);
        mElapsedTimeTv = view.findViewById(R.id.elapsed_timeTv);
        mButtonReset = view.findViewById(R.id.buttonReset);
        mButtonUpload = view.findViewById(R.id.buttonUpload);


        /*
        Set an onClickListener on the start button. When the start button is clicked,
        the method will be called.
        */
        mButtonStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Permissions check
                // If permissions are not granted the if statement is validated and a permissions request is called
                // This displays a window to the user to allow them to allow permissions - as they are essential to the application
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_FINE_LOCATION_CODE
                    );

                    // This else if avoid a fatal except caused by the stopwatch class if the record button
                    // is pressed more than once
                } else if (!isLocationServiceRunning()) {
                    // User has already granted permissions so the app will function properly
                    // Start the stopwatch
                    watch.start();
                    UUID uuid = UUID.randomUUID();
                    sessionID = uuid.toString();
                    uploadSessionActivity.receiveSessionID(sessionID);
                    // Call the method starLocationService
                    startLocationService();
                } else if (isLocationServiceRunning()) {
                    Toast.makeText(getActivity(), "Session has already been started!", Toast.LENGTH_SHORT).show();
                }

            }
        });

         /*
        Set an onClickListener on the stop button. When the start button is clicked,
        the method will be called.
        */
        mButtonStopLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationServiceRunning()) {
                    // Call stop location service method - stops the location service
                    stopLocationService();
                    // Stopwatch stopped, elapsed time TextView will not update while location service not running
                    watch.stop();

                } else {
                    // Create text to tell user a service cannot be stopped if it is not running
                    Toast.makeText(getActivity(), "No service is running, cannot perform action!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Button reset text views and stopwatch
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Check that the location service is not running
                if (!isLocationServiceRunning()) {
                    // Call the reset method
                    watch.reset();
                    // Set the all the text views to 0
                    mCurrent_speedTv.setText("Speed is: 0 knots");
                    mBearingTv.setText("Direction: 0" + "\u00B0");
                    mElapsedTimeTv.setText("Elapsed time: 0");


                    // Clear all recorded data from database
                    latLongArray.clear();
                    FirebaseUser user = mAuth.getCurrentUser();

                    assert user != null;
                    String uid = user.getUid();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    // Path toe store user data named "Users"
                    DatabaseReference reference = database.getReference("Users" + uid);
                    // Put data within HashMap in database
                    reference.child("Sessions").child(String.valueOf(sessionID)).setValue(latLongArray);

                } else if (isLocationServiceRunning()) {
                    // Inform the user that the location service must be stopped before resetting
                    Toast.makeText(getActivity(), "Stop the session before resetting!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UploadSessionActivity.class));
            }
        });






        /*
        The data sent from the service is handled below
         */
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Received data is now assigned to variables
                        double latitude = intent.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0);
                        double longitude = intent.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0);
                        float speed = intent.getFloatExtra(LocationService.EXTRA_SPEED, 0);
                        float bearing = intent.getFloatExtra(LocationService.EXTRA_BEARING, 0);

                        // Stopwatch timer is retrieved
                        time = watch.getTime();

                        // Calculate the conversion from m/s to knots
                        // assign calculated value to variable
                        speedInKnots = speed * 1.194;
                        // Cast the float to double
                        doubleBearing = bearing;
                        // Calculate conversion from milli-seconds to seconds
                        // assign calculated value to variable
                        timeInSeconds = time / 1000;

                        // Log the location data
                        Log.d(TAG, "onReceive:  Lat: " + latitude + ", Long: " + longitude);
                        Log.d("Speed_before_conversion", "onReceive: Speed before conversion " + speed);

                        // Update the text views displayed in record fragment
                        // round() method called, double value parsed to it and the number of
                        // decimal places after the value›
                        mCurrent_speedTv.setText("Speed is: " + round(speedInKnots, 2) + " knots");
                        mBearingTv.setText("Direction: " + round(doubleBearing, 2) + "\u00B0");
                        mElapsedTimeTv.setText("Elapsed time: " + round(timeInSeconds, 2) + " s");



                        // Get the current user
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Get current user id
                        assert user != null;
                        String uid = user.getUid();

                        // Log used for debug
                        Log.d(TAG, "Current user uid: " + uid);

                        // Assign the current lat and long to a new LatLng object
                        latLng = new LatLng(latitude, longitude);

                        // Add the LatLng object to the LatLng arraylist
                        latLongArray.add(latLng);

                        // Add speed data to the float arraylist
                        speedData.add(speed);




                        // Instantiate Firebase database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        // Path to store user data named "Users"
                        DatabaseReference reference = database.getReference("Users/" + uid);




                        // Put data within arraylist in database
                        reference.child("Sessions").child(sessionID).setValue(latLongArray);


                        DatabaseReference reference1 = database.getReference("Users/" + uid + "Sessions");
                        // Put speed data into database
                        reference1.child(sessionID).child("Speed").setValue(speedData);

                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
        return view; // End of broadcaster receiver
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_FINE_LOCATION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(getActivity(), "Permissions denied - " +
                                "location permissions are required to use core functionality of this app",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent startIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            startIntent.setAction(ConstantsForLocationService.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(startIntent);
            Toast.makeText(getActivity(), "Location service started", Toast.LENGTH_SHORT).show();

        }

    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Toast.makeText(getActivity(), "Location service stopped", Toast.LENGTH_SHORT).show();
            Log.d("LocationService", "stopLocationService: Sending data to service.....");
            Intent stopIntent = new Intent(getActivity(), LocationService.class);
            stopIntent.setAction(ConstantsForLocationService.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(stopIntent);


        }
    }

    // Rounding helper method
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}



















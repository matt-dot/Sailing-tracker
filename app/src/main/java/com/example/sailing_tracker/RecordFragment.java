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

import org.apache.commons.lang3.time.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    Button mButtonStartLocationUpdates, mButtonStopLocationUpdates;





    public RecordFragment(){
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Assign view to the inflated xml layout
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // Instantiate the stopwatch class
        watch = new StopWatch();

        // Assign values
        mButtonStartLocationUpdates = view.findViewById(R.id.buttonRecord);
        mButtonStopLocationUpdates = view.findViewById(R.id.buttonStop);
        mCurrent_speedTv = view.findViewById(R.id.current_speedTv);
        mBearingTv = view.findViewById(R.id.bearingTv);
        mElapsedTimeTv = view.findViewById(R.id.elapsed_timeTv);


        /*
        Set an onClickListener on the start button. When the start button is clicked,
        the method will be called.
        */
        mButtonStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log used for debugging
                Log.d(TAG, "Click of start location service");
                // Permissions check
                // If permissions are not granted the if statement is validated and a permissions request is called
                // This displays a window to the user to allow them to allow permissions - as they are essential to the application
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_FINE_LOCATION_CODE
                    );
                } else {
                    // User has already granted permissions so the app will function properly
                    // Start the stopwatch
                    watch.start();
                    // Call the method starLocationService
                    startLocationService();
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
                if(isLocationServiceRunning()){
                    // Call stop location service method - stops the location service
                    stopLocationService();

                }
                // Stopwatch stopped, elapsed time TextView will not update while location service not running
                watch.stop();
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

                        double latitude = intent.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0);
                        double longitude = intent.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0);
                        float speed = intent.getFloatExtra(LocationService.EXTRA_SPEED,0);
                        float bearing = intent.getFloatExtra(LocationService.EXTRA_BEARING, 0);

                        time = watch.getTime();

                        speedInKnots = speed * 1.194;
                        doubleBearing = (((double) bearing));
                        timeInSeconds = time /1000;


                        // Log the location data
                        Log.d(TAG, "onReceive:  Lat: " + latitude + ", Long: " + longitude);
                        Log.d("Speed_before_conversion", "onReceive: Speed before conversion " + speed);
                        mCurrent_speedTv.setText("Speed is: " + round(speedInKnots,2) + " knots");
                        mBearingTv.setText("Direction: " + round(doubleBearing, 2) + "\u00B0");
                        mElapsedTimeTv.setText("Elapsed time: " + round(timeInSeconds,2) + " s");




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
    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent startIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            startIntent.setAction(ConstantsForLocationService.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(startIntent);
            Toast.makeText(getActivity(), "Location service started", Toast.LENGTH_SHORT).show();

        }
    }
    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Toast.makeText(getActivity(), "Location service stopped", Toast.LENGTH_SHORT).show();
            Log.d("LocationService", "stopLocationService: Sending data to service.....");
            Intent stopIntent = new Intent(getActivity(), LocationService.class);
            stopIntent.setAction(ConstantsForLocationService.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(stopIntent);

        }
    }
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }



    }
















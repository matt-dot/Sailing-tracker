package com.example.sailing_tracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;



public class LocationService extends Service {

    public static final String
            ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast",
            EXTRA_LATITUDE = "extra_latitude",
            EXTRA_LONGITUDE = "extra_longitude";

    private static final int
            MIN_TIME = 2000,
            MIN_DISTANCE = 1;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();



        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("Broadcast", "Success: Send broadcast parsing location");
                        sendBroadcastMessage(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }
        );
    }



    // Location callback is called evertime
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // If both locationResult and last location are not null then...
            // This avoids null pointer exceptions as there are instances where last known location
            // will be null
            if(locationResult != null && locationResult.getLastLocation() != null){
                // ...assign the user current location to lat and long variables
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();



                // Output to log
                Log.d("LOCATION_UPDATE", "Lat: "+ latitude + "," + "Long: " + longitude);

            }

            }
        };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    // TODO: 07/01/2021 Comment me 
    @SuppressLint("MissingPermission")
    private void startLocationService(){
        // Set the id of the channel
        String channelId = "location_notification_channel";
        // Init NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);


        // TODO: 07/01/2021 Read about channels and then understand below
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null){
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "LocationService",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(5000); // Store me in a constant
            locationRequest.setFastestInterval(2000); // Store me in a constant
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, locationCallback, null); // May cause error

            startForeground(ConstantsForLocationService.LOCATION_SERVICE_ID, builder.build());


            }


        }





    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
             String action = intent.getAction();
             if (action != null){
                 if(action.equals(ConstantsForLocationService.ACTION_START_LOCATION_SERVICE)){
                     startLocationService();
                 } else if (action.equals(ConstantsForLocationService.ACTION_STOP_LOCATION_SERVICE)) {
                     stopLocationService();
                 }
             }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendBroadcastMessage(Location location) {
        if (location != null) {
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
            intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(intent);
        }
    }
}







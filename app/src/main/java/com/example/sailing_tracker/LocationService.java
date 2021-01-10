package com.example.sailing_tracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationService extends Service {

    private static final String TAG = "LocationService";
    //double speedInKnots;

    public static final String
            ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast",
            EXTRA_LATITUDE = "extra_latitude",
            EXTRA_LONGITUDE = "extra_longitude",
            EXTRA_SPEED = "extra_speed",
            EXTRA_BEARING = "extra_bearing";

    // Declare the constants which hold the frequency of the call backs
    // Meaning every 100 ms the callback is called.
    public static final int LOCATION_REQUEST_DEFAULT_INTERVAL = 100, LOCATION_REQUEST_FASTEST_INTERVAL = 10;


    Location location;




    // Location callback every
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // If both locationResult and last location are not null then...
            // This avoids null pointer exceptions as there are instances where last known location
            // will be null

            if(locationResult != null && locationResult.getLastLocation() != null){
                location = locationResult.getLastLocation();
                // Parse the location to the sendBroadcastMessage method to send data
                // to receiver in RecordFragment classZ
                sendBroadcastMessage(location);
            }

            }
        };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @SuppressLint("MissingPermission")
    private void startLocationService(){
        // Set the id of the channel
        String channelId = "location_notification_channel";

        // Init NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create new intent named resultIntent
        Intent resultIntent = new Intent();
        // New pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Instantiate the builder which is used to create the notification bound to the service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );


        // Builds the notification that the service is bound to
        builder.setSmallIcon(R.drawable.ic_sailing_app);
        builder.setContentTitle("Recording session");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        // Check to see whether build is greater than or equal to Oreo due to compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null){
                // Instantiate a new notification channel
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "LocationService",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Instantiate location request
            LocationRequest locationRequest = new LocationRequest();

            // Set the parameters of the location request
            // Interval is the frequency of the callback
            locationRequest.setInterval(LOCATION_REQUEST_DEFAULT_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);

            // Tells fusedLocationClientProvider to use services which provide high
            // levels of accuracy as fusedLocationClientProvider includes more than one
            // method of recording location
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            // Get the fused location provider from location services class and
            // call the requestLocationUpdates method
            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, locationCallback, null);

            // Start the foreground service
            startForeground(ConstantsForLocationService.LOCATION_SERVICE_ID, builder.build());
            }
        }





    private void stopLocationService(){
        // Call the location services class and get the FusedLocationProviderClient
        // call the remove location services class relating to location callback
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        // Remove the notification
        stopForeground(true);
        // End service
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



    // Method to send data service to receiver in the RecordFragment class
    // Location is parsed to the method
    private void sendBroadcastMessage(Location location) {
        if (location != null) {
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            // Add extra data to the intent
            // When the intent is parsed so is the extra data
            intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
            intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
            intent.putExtra(EXTRA_SPEED, location.getSpeed());
            intent.putExtra(EXTRA_BEARING, location.getBearing());
            // Send the data to receiver
            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(intent);
        }
    }
}







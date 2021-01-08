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
import android.util.Log;

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


    Location location;


    // Location callback is called evertime
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // If both locationResult and last location are not null then...
            // This avoids null pointer exceptions as there are instances where last known location
            // will be null
            if(locationResult != null && locationResult.getLastLocation() != null){

                location = locationResult.getLastLocation();
                // ...assign the user current location to lat and long variables

                // Variables needed for log
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                double speed = ((double) locationResult.getLastLocation().getSpeed());
                double bearing = locationResult.getLastLocation().getBearing();



                // Output to log
                Log.d("Broadcast", "Lat: "+ latitude + "," + "Long: " + longitude);
                Log.d("Broadcast", "Success: Send broadcast parsing location");
                Log.d("Broadcast", "Speed: " + speed);


                // Broadcast the current location to the record fragment
                sendBroadcastMessage(location);
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
        builder.setSmallIcon(R.drawable.ic_sailing_app);
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
            locationRequest.setInterval(100); // Store me in a constant
            locationRequest.setFastestInterval(100); // Store me in a constant
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, locationCallback, null); // May cause error

            startForeground(ConstantsForLocationService.LOCATION_SERVICE_ID, builder.build());


            }


        }





    private void stopLocationService(){
        Log.d(TAG, "stopLocationService: Stopping service");
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
            intent.putExtra(EXTRA_SPEED, location.getSpeed());
            intent.putExtra(EXTRA_BEARING, location.getBearing());
            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(intent);
        }
    }
}







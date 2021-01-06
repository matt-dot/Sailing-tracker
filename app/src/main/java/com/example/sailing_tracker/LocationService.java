package com.example.sailing_tracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class LocationService extends Service {




    private LocationCallback locationCallback = new LocationCallback() {
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
}




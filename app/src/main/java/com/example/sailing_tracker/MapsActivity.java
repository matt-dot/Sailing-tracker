package com.example.sailing_tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSIONS_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    FirebaseAuth firebaseAuth;
    LocationManager locationManager;
    MarkerOptions markerOptions;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Toolbar mToolBar = findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolBar);



        firebaseAuth = firebaseAuth.getInstance();

        // Set default toolbar value
        mToolBar.setTitle("Home");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markerOptions = new MarkerOptions().position(new LatLng(0, 0));

        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSIONS_ALL);
        } else requestLocation();
        if (!isLocationEnabled()) {
            showAlert(1);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers){
            Intent myIntent = new Intent(MapsActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return true;
    }

    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in stay here
            // Set email of logged in user
        }
        else{
            // User is not signed in
            startActivity(new Intent(MapsActivity.this, MainActivity.class));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

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

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.v("mylog", "permission granted");
                return true;
            } else {
                Log.v("mylog", "permission denied");
                return false;
            }

        }
        return false;
    }

    private void showAlert(final int status){
        String message, title, btnText;
        if(status == 1){
            message = "Your location settings are set to to off \n Please enable to use " +
                    "app";
            title = "Enable location";
            btnText = "Location settings";
        } else {
            message = "Please allow app to access your location";
            title = "Permission access";
            btnText = "Grant";
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (status == 1) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS, PERMISSIONS_ALL);
                            }
                        }


                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
    dialog.show();

    }




}
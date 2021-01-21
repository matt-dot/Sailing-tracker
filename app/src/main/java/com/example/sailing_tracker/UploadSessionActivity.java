 package com.example.sailing_tracker;

 import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

 import com.google.android.gms.maps.CameraUpdate;
 import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
 import com.google.android.gms.maps.model.Dash;
 import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

 public class UploadSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

     GoogleMap mMap;

     FirebaseAuth mAuth;


     // Views
     EditText titleEt, descriptionEt;

     Button publishButton;

     String email, uid, name;

     DatabaseReference dbRef;
     private DatabaseReference mDatabase;

     double lat, lon;







     ProgressDialog pd;

     Toolbar mToolBar;


     ArrayList<LatLng> coordinateArrayList = new ArrayList<>();


     public static String sessionIDForPath;


     public void receiveSessionID(String receivedSessionID) {
         Log.d("Reeee", "receiveSessionID: " + receivedSessionID);

         sessionIDForPath = receivedSessionID;

         Log.d("Reeee", "Session: " + sessionIDForPath);


     }


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_publish_session_acitivity);

         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.map);
         assert mapFragment != null;
         mapFragment.getMapAsync(this);


         pd = new ProgressDialog(this);
         mAuth = FirebaseAuth.getInstance();
         FirebaseUser user = mAuth.getCurrentUser();

         assert user != null;
         final String uid = user.getUid();


         // Toolbar init
         mToolBar = findViewById(R.id.my_toolbar);
         setSupportActionBar(mToolBar);
         // Set default toolbar value
         //mToolBar.setTitle("Home");


         mAuth = FirebaseAuth.getInstance();
         checkUserStatus();


         dbRef = FirebaseDatabase.getInstance().getReference("Users" + uid);
         Query query = dbRef.orderByChild(uid).equalTo(uid);
         query.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     name = "" + ds.child("name").getValue();
                     email = "" + ds.child("email").getValue();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         // Assign variables to layout elements
         titleEt = findViewById(R.id.pTitleEt);
         descriptionEt = findViewById(R.id.pDescriptionEt);
         publishButton = findViewById(R.id.pPublishButton);

         publishButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // Get data (title, description) from editText
                 String title = titleEt.getText().toString().trim();
                 String description = descriptionEt.getText().toString().trim();

                 // Method call, parsing what was entered into to above elements
                 uploadData(title, description);

             }
         });

         Log.d("SessionIDCheck", "OnCreate session ID check " + sessionIDForPath);
     }


     private void uploadData(final String title, final String description) {
         pd.setMessage("Publishing session...");
         pd.show();



         HashMap<Object, String> hashMap = new HashMap<>();
         hashMap.put("uid", uid);
         hashMap.put("uName", name);
         hashMap.put("uEmail", email);
         hashMap.put("pTitle", title);
         hashMap.put("pDescription", description);


         // Put data into this reference
         mDatabase.child("Users").child(uid).child("Sessions").child(sessionIDForPath).child("PostData").setValue(hashMap);
         Toast.makeText(UploadSessionActivity.this, "Session published", Toast.LENGTH_SHORT).show();


         startActivity(new Intent(UploadSessionActivity.this, MainActivity.class));





     }














    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            mAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers){
            Intent myIntent = new Intent(UploadSessionActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in stay here
            // Set email of logged in user
            email = user.getEmail();
            uid = user.getUid();
        }
        else{
            // User is not signed in
            startActivity(new Intent(UploadSessionActivity.this, MainActivity.class));

        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("SessionIDCheck", "onMapReady: " + sessionIDForPath);
        Log.d("SessionIDCheck", "onMapReady() returned: " + sessionIDForPath);

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mMap = googleMap;
        mDatabase.child("Users").child(uid).child("Sessions").child(sessionIDForPath).addValueEventListener(new ValueEventListener(){
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot){
                for(DataSnapshot data: dataSnapshot.getChildren()){



                    // Get LatLng object from the ArrayList stored in db
                    // and assign to a variable
                    Object latitude = data.child("latitude").getValue();
                    Object longitude = data.child("longitude").getValue();

                    if(latitude != null && longitude != null) {
                        // Parse the object to double so it can be added to
                        // a local double ArrayList
                        lat = Double.parseDouble(latitude.toString());
                        lon = Double.parseDouble(longitude.toString());


                        // Meaningless logs
                        Log.d("LatAndLong123", "Latitude: " + latitude);
                        Log.d("LatAndLong123", "Longitude: " + longitude);

                        // Store the lat and long data into array list
                        coordinateArrayList.add(new LatLng(lat, lon));
                        // Move the camera to the last coordinate of the session
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 11));
                    }



                }

                // Init Polyline options
                PolylineOptions polylineOptions = new PolylineOptions();


                // Create polyline options with existing LatLng ArrayList
                // and configure color and width
                polylineOptions.addAll(coordinateArrayList);
                polylineOptions
                        .width(5)
                        .color(Color.RED);

               // Adding multiple points in map using polyline and ArrayList
                mMap.addPolyline(polylineOptions);



            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }


        });





    }










}










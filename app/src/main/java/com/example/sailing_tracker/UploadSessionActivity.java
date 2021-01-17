 package com.example.sailing_tracker;

 import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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




    ProgressDialog pd;

    Toolbar mToolBar;


    PolylineOptions polyline1;
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();







    public static String sessionIDForPath;









    public void receiveSessionID(String receivedSessionID){
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




        // Toolbar init
        mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        // Set default toolbar value
        //mToolBar.setTitle("Home");


        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt  = findViewById(R.id.pDescriptionEt);
        publishButton = findViewById(R.id.pPublishButton);
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = dbRef.orderByChild("email").equalTo("email");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        publishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Get data (title, description) from editText
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();

             uploadData(title, description, "noImage" );

            }
        });

        Log.d("SessionIDCheck", "OnCreate session ID check " + sessionIDForPath);
    }




    private void uploadData(final String title, final String description, String uri) {
        pd.setMessage("Publishing session...");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;


        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUrl = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            // Url received upload post to firebase

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("uid", uid );
                            hashMap.put("uName", name);
                            hashMap.put("uEmail", email);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("pTitle", title);
                            hashMap.put("pDescription", description);
                            hashMap.put("pTime", timeStamp);


                            // path to store post data

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            // Put data into this reference
                            ref.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Added to database
                                            pd.dismiss();
                                            Toast.makeText(UploadSessionActivity.this, "Session published",Toast.LENGTH_SHORT).show();

                                        }
                                    }) .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to add post to database
                                    pd.dismiss();
                                    Toast.makeText(UploadSessionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });






                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UploadSessionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


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

                    Object latitude= data.child("latitude").getValue();
                    Object longitude = data.child("longitude").getValue();



                    Double lat = (Double) latitude;
                    Double lon = (Double) longitude;

                    Log.d("LatAndLong", "Latitude: " + lat);
                    Log.d("LatAndLong", "Longitude: " + lon);

                    // Store the lat and long data into array list
                    coordList.add(new LatLng(lat, lon));

                }
                FusedLocationProviderClient fusedLocationProviderClient;

                PolylineOptions polylineOptions = new PolylineOptions();

                // Create polyline options with existing LatLng ArrayList
                polylineOptions.addAll(coordList);
                polylineOptions
                        .width(5)
                        .color(Color.RED);

               // Adding multiple points in map using polyline and arraylist
                mMap.addPolyline(polylineOptions);






            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }


        });





    }








}










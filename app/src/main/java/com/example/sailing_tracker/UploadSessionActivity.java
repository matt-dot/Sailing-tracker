package com.example.sailing_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

import java.util.HashMap;

public class UploadSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    FirebaseAuth mAuth;


    // Views
    EditText titleEt, descriptionEt;

    Button publishButton;

    String email, uid, name;

    DatabaseReference dbRef;
    DatabaseReference dbLocationRef;


    DataSnapshot dataSnapshot;


    ProgressDialog pd;

    Toolbar mToolBar;




    public static String sessionIDForPath;



    Double latZero;
    Double indexOne;








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

        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");



        dbLocationRef = FirebaseDatabase.getInstance().getReference("Users/Sessions");


        Query locationQuery = dbLocationRef.orderByChild("Sessions").equalTo(sessionIDForPath);
        locationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check  until required data got
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    indexOne = Double.valueOf(""+ds.child("0").getValue());
                }
                Log.d("OnDataChange", "ARRAY VALUE: " + indexOne);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.d("SessionIDCheck", "onMapReady() returned1: " + sessionIDForPath);
    }








}










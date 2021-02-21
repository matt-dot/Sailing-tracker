package com.example.sailing_tracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class UploadSessionActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Google map declaration
    GoogleMap mMap;

    FirebaseAuth mAuth;



    String timeStamp;

    // Views and elements
    EditText titleEt, descriptionEt;
    Button publishButton;

    TextView pTimeTv;

    // Variables to hold data retrieved from db
    String email, uid, name, dp;

    public static double averageSpeed;






    // Database reference

    private DatabaseReference mDatabase;

    // Variables to hold data retrieved from
    double lat, lon;


    ProgressDialog pd;

    Toolbar mToolBar;


    ArrayList<LatLng> coordinateArrayList = new ArrayList<>();


    public static String sessionIDForPath;

    public void receiveAverageSpeed(double receivedAverageSpeed) {
        // Assign the sessionID from RecordFragment to variable
        // This will be used for database path
        averageSpeed = receivedAverageSpeed;


        // Log to make sure the session ID matches the session ID in
        // generated in record fragment
        Log.d("AverageSpeedCheck", "Session: " + averageSpeed);
    }



    public void receiveSessionID(String receivedSessionID) {
        // Assign the sessionID from RecordFragment to variable
        // This will be used for database path
        sessionIDForPath = receivedSessionID;

        // Log to make sure the session ID matches the session ID in
        // generated in record fragment
        Log.d("SessionIdCheck", "Session: " + sessionIDForPath);

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

        // Init progress dialog, firebase auth and firebase user
        pd = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        // Get the current user and assign to variable
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        // Get the uid of the user





        // Toolbar init
        mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);


        // Instantiate firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Call check user status
        checkUserStatus();

        // Create a database reference
        // Reference to User/ uid (of current user)
        mDatabase = getInstance().getReference("Users");





        // Get the display picture of the user
        Query imageQuery = mDatabase.orderByChild("email").equalTo(user.getEmail());
        imageQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check  until required data got
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Get data
                    dp = "" + ds.child("image").getValue().toString();

                }

            }





            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast to explain to user the error
                Toast.makeText(UploadSessionActivity.this, "Error retrieving data from the database ", Toast.LENGTH_SHORT).show();

            }
        });





        // Assign variables to layout elements
        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        publishButton = findViewById(R.id.pPublishButton);
        pTimeTv = findViewById(R.id.pTimeTv);

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data (title, description) from editText
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();

                // Method call, parsing what was entered into to above elements
                uploadData(title, description, averageSpeed);

            }
        });

    }

    // Method which uploads data for the post
    private void uploadData(final String title, final String description, double averageSpeed) {
        // Show the status of upload
        pd.setMessage("Publishing session...");
        pd.show();

        timeStamp = String.valueOf(System.currentTimeMillis());

        // Assign the relevant data to hash map
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("uEmail", email);
        hashMap.put("pTitle", title);
        hashMap.put("pDescription", description);
        hashMap.put("pTime", timeStamp);
        if (dp == null){
            dp = "null";
        }
        hashMap.put("uDp", dp);
        hashMap.put("pSessionID", sessionIDForPath);
        hashMap.put("pSpeed", String.valueOf((averageSpeed)));
        hashMap.put("pLikes", "0");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        // Put data into this reference
        databaseReference.child(sessionIDForPath).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(UploadSessionActivity.this, "Session published", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadSessionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Move to home fragment
        startActivity(new Intent(UploadSessionActivity.this, DashboardActivity.class));
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Inflate the options menu method
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_signOut) {
            mAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_showUsers) {
            Intent myIntent = new Intent(UploadSessionActivity.this, UsersActivity.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    // Check user status helper method
    private void checkUserStatus() {
        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in stay here
            // Set email of logged in user
            email = user.getEmail();
            uid = user.getUid();
        } else {
            // User is not signed in
            startActivity(new Intent(UploadSessionActivity.this, SignInActivity.class));

        }
    }





    // Generate map
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Instantiate the firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Instantiate google map
        mMap = googleMap;
        Log.i("SessionID", "onMapReady: " + sessionIDForPath);
        // Path to read data from
        mDatabase.child("Sessions").child(sessionIDForPath).child("LatLngData").addValueEventListener(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // Get LatLng object from the ArrayList stored in db
                    // and assign to a variable
                    Object latitude = data.child("latitude").getValue();
                    Object longitude = data.child("longitude").getValue();

                    if (latitude != null && longitude != null) {
                        // Parse the object to double so it can be added to
                        // a local double ArrayList
                        lat = Double.parseDouble(latitude.toString());
                        lon = Double.parseDouble(longitude.toString());

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
                // Toast to user showing database error
                Toast.makeText(UploadSessionActivity.this, "Database error has occurred", Toast.LENGTH_SHORT).show();

            }
        });


    }

}










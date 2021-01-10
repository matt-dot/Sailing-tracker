package com.example.sailing_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class PublishSessionActivity extends AppCompatActivity {


    FirebaseAuth mAuth;


    // Views
    EditText titleEt, descriptionEt;
    ImageView imageIv;
    Button publishButton;

    String email, uid, name;

    DatabaseReference dbRef;


    DataSnapshot dataSnapshot;


    ProgressDialog pd;

    Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_session_acitivity);
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
                                            Toast.makeText(PublishSessionActivity.this, "Session published",Toast.LENGTH_SHORT).show();

                                        }
                                    }) .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to add post to database
                                    pd.dismiss();
                                    Toast.makeText(PublishSessionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });






                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PublishSessionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent myIntent = new Intent(PublishSessionActivity.this, UsersActivity.class);
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
            startActivity(new Intent(PublishSessionActivity.this, MainActivity.class));

        }
    }
}
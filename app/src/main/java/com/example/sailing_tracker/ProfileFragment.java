package com.example.sailing_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Adapters.AdapterUserSessions;
import com.example.sailing_tracker.Models.ModelPost;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class ProfileFragment extends Fragment {

    // Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Storage instantiation
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://sailing-tracker-ed506.appspot.com");

    // Path of profile pic storage location
    String storagePath = "User_Profile_Picture/";

    // Views from xml
    ImageView profilePicIv;
    TextView nameTv, emailTv, boatClassTv;
    FloatingActionButton floatingActionButton;

    // Progress dialog
    ProgressDialog progressDialog;

    // Permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    // Arrays of permissions to be requested
    String[] cameraPermissions;
    String[] storagePermissions;

    // URI of picked image
    Uri image_uri;

    // For checking profile picture
    String profilePicture;


    private RecyclerView recyclerView;
    private List<ModelPost> postList;
    AdapterUserSessions adapterUserSessions;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = getInstance().getReference("Users");
        storageReference = storage.getReference("filePathAndName");

        // Init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Init views
        profilePicIv = view.findViewById(R.id.profilePicIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        boatClassTv = view.findViewById(R.id.boatClassTv);


        // init progress dialog
        progressDialog = new ProgressDialog(getActivity());

        /*
        Get info of currently signed in user. Using email.
        Using orderByChild query show detail from a node
        whose key named email has value equal to email currently signed in
        Search all nodes, where key matches, details will be retrieved.
         */

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check  until required data got
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    String boatClass = "" + ds.child("boatClass").getValue();

                    // Set data
                    emailTv.setText(email);

                    if(email != null && boatClass != null){
                        nameTv.setText(name);
                        boatClassTv.setText(boatClass);

                    } else {
                        nameTv.setText("Click the edit icon to set your name");
                        boatClassTv.setText("Click the edit icon to set your boat class");
                    }

                    try {
                        if(image != null) {
                            // If image received set to profilePic image view
                            Picasso.get().load(image).into(profilePicIv);
                        }
                    } catch (Exception e) {
                        // Set default if exception
                        Picasso.get().load(R.drawable.id_default_profile).into(profilePicIv);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        // recycler view and its properties
        recyclerView = view.findViewById(R.id.usersSessionRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // Shows newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        // init post list
        postList = new ArrayList<>();

        // Call load post method
        //loadPost();

        return view;

    }

    private boolean checkStoragePermissions() {
        // Check if storage permission enabled or not
        // Return true if enabled›
        // Return false if disabled

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestStoragePermissions() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        // Check if storage permission enabled or not
        // Return true if enabled
        // Return false if disableD
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    private void requestCameraPermissions() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    /*
    Creates the interface for the user to interact with to edit their data
    through the use of a profile dialog.
    Three method calls are contained within to handle the three options available to the user
    Only two methods as the logic for updating name/ boat class can be reused
     */
    private void showEditProfileDialog() {
        // Edit profile pic
        // Edit name
        // Edit boat class

        String[] options = {"Edit profile picture", "Edit name", "Edit boat class"};
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit profile");

        // Configure to the logic for what happens on click of each option
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle item clicks
                if (which == 0) {
                    // Edit Profile picture clicked
                    progressDialog.setMessage("Updating profile picture");
                    profilePicture = "image";
                    // Method call to show the profile picture upload options
                    showProfilePicDialog();
                } else if (which == 1) {
                    // Edit name picked
                    progressDialog.setMessage("Updating name");
                    // Method call to show dialog to edit name or boatClass, parsing name
                    showNameBoatClassUpdateDialog("name");
                } else if (which == 2) {
                    // Edit boat class picked
                    progressDialog.setMessage("Updating boat class");
                    // Method call to show dialog to edit name or boatClass, parsing boatClass
                    showNameBoatClassUpdateDialog("boatClass");
                }
            }
        });
        // Create and show dialog
        builder.create().show();
    }

    private void showNameBoatClassUpdateDialog(final String key) {
        String[] options = {"Edit boat class", "Edit name"};
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);

        // Set linear layout

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        // Add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);


        // Add button to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Input text from editText
                String value = editText.getText().toString().trim();
                // Presence check
                if (!TextUtils.isEmpty(value)) {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(getActivity(), "Please enter " + key + "", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Add button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        // Create and show dialog
        builder.create().show();

    }

    // Method providing the user with the two options
    // on where they would like to upload their photo from
    private void showProfilePicDialog() {
        String[] options = {"Camera", "Gallery"};
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Image from");
        // Alert dialog set to the defined
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle item clicks
                if (which == 0) {
                    // Camera picked
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    // Gallery picked
                    if (!checkStoragePermissions()) {
                        requestStoragePermissions();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        // Create and show dialog
        builder.create().show();
    }


    // The method below handles the two permissions request - storage and camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        // Switch case statement based on the data which has been parsed to the method
        // It checks what the value of the request code is, and if it is equal to the
        // constants then we know that that is the permission request.›
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    }
                    // Permission is granted. Continue the action
                } else {
                    // Permission denied
                    Toast.makeText(getActivity(), "Please enable permissions", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case STORAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();

                    }
                    // Permission is granted. Continue the action
                } else {
                    // Permission denied
                    Toast.makeText(getActivity(), "Please enable permissions", Toast.LENGTH_SHORT).show();
                }
            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void pickFromGallery() {
        // Pick an image from phone gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Pick image"), IMAGE_PICK_GALLERY_CODE);


    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");

        // Put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Method to be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // Image picked fromm gallery, get uri of image
                image_uri = data.getData();
                uploadProfilePicture(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // Image picked from camera, get uri of image

                uploadProfilePicture(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePicture(Uri image_uri) {
        UploadTask uploadTask;

        String filePathAndName = storagePath + "" + profilePicture + "_" + user.getUid();
        StorageReference storageReference2 = storage.getReference();
        final StorageReference fileRef = storageReference2.child(filePathAndName);


        uploadTask = fileRef.putFile(image_uri);

        final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // Check if image is uploaded and url received
                            /*
                            First parameter is profilePicture that has value "image"
                            which is a key in the user's database where
                            url of the image will be saved to
                            Second parameter contains the url
                            of the image stored in th firebase storage, this url will be saved as value
                            against key "image"
                             */

                    // Image uploaded successfully
                    // Add update the url in user's database
                    HashMap<String, Object> results = new HashMap<>();

                    assert downloadUri != null;
                    results.put(profilePicture, downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Url has successfully been uploaded
                                    // Dismiss the progress bar
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Image updated...", Toast.LENGTH_SHORT).show();

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error has occurred adding url to database of user
                            // Dismiss progress bar
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error updating image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error has occurred - inform user through toast
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                }

            }



    /*

    public void loadPost() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        final String uid = firebaseUser.getUid();

        // Path of all posts
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Posts");

        Query queryForCurrentUser = mDatabase1.orderByChild("uEmail").equalTo(firebaseUser.getEmail());
        // Get all data from this reference
        queryForCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    // Adapter
                    adapterUserSessions = new AdapterUserSessions(getActivity(), postList);
                    // Set adapter to recycler view
                    recyclerView.setAdapter(adapterUserSessions);


                }
                Log.d("PostListData", "onDataChange: " + postList);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // In case of error
                // Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

     */



        });
    }
}
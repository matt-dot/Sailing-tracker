package com.example.sailing_tracker.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Models.ModelPost;
import com.example.sailing_tracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;

    // List to hold each model post
    List<ModelPost> postList;
    View view;

    // Database reference variables
    private final DatabaseReference likesReference;
    private final DatabaseReference postsReference;

    boolean mProcessLike = false;

    // Variable to hold uid of current user
    String myUID;


    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        // Get the uid of the currently logged in user
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Define a database reference which points to the Likes node
        likesReference = FirebaseDatabase.getInstance().getReference().child("Likes");
        // Define a database reference which points to the Posts node
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_post.xml
        view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);

    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {


        // Below data from the postList list is assigned to variables whcih will be used to
        // set the data of each post
        String uEmail = postList.get(position).getuEmail();
        String uDp = postList.get(position).getuDp();
        final String pTitle = postList.get(position).getpTitle();
        final String pDescription = postList.get(position).getpDescription();
        String pTimeStamp = postList.get(position).getpTime();
        final String pSessionID = postList.get(position).getpSessionID();
        final String pSpeed = postList.get(position).getpSpeed();
        String pLikes = postList.get(position).getpLikes();


        // Covert timestamp into dd/mm//yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        // The value of each attribute is now assigned to the previously retrieved
        // data from the postList
        holder.pLikesTv.setText(pLikes + " Likes");
        holder.pAverageSpeedTv.setText(pSpeed);
        holder.uEmailTv.setText(uEmail);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.sessionID = pSessionID;
        Log.d("Time", "onBindViewHolder: " + pTime);
        setLikes(holder, pSessionID);

        // Load the user display picture and place in image view
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_user_image).into(holder.uPictureIv);
        } catch (Exception e){

        }




        // Sets on click listener for the like button
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the value of the pLikes attribute stored in postList from the server
                final int pLikes = Integer.parseInt((postList.get(position).getpLikes()));
                mProcessLike = true;
                // Retrieve the value of the sessionID, which is equal to the postID
                final String postID = postList.get(position).getpSessionID();
                // Add value event listener to get the data from the database reference
                likesReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            // If the Likes node does not contain the UID of the currently signed in
                            // user then they are able to like it and so their uid will be added to the
                            // Likes node and the value of pLikes incremented by 1.
                            if (!snapshot.child(postID).hasChild(myUID)) {
                                postsReference.child(postID).child("pLikes").setValue("" + (pLikes + 1));
                                likesReference.child(postID).child(myUID).setValue("Liked");
                                mProcessLike = false;
                            } else {
                                // The likes node contains the UID of the currently signed in
                                // which means the user has already liked it.
                                // Users may only like a post once
                                // The following logic removes the UID from the Likes node and
                                // decrements the value of pLikes by 1
                                postsReference.child(postID).child("pLikes").setValue("" + (pLikes - 1));
                                likesReference.child(postID).child(myUID).removeValue();
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Get the thrown error message
                        Toast.makeText(context, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Sets on click listener for the share button
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // define the view to convert into a bitmap
                View contentView =  view.findViewById(R.id.postMap);
                // enable drawing cache
                contentView.setDrawingCacheEnabled(true);
                // Convert the content view into a bitmap using drawing cache
                Bitmap bitmap = contentView.getDrawingCache();

                // Call sharePost method parsing the relevant variables
                sharePost(pTitle, pSpeed, pDescription, bitmap);

            }
        });








    }

    // This method handles the sharing of posts to external applications

    private void sharePost(String pTitle, String pDescription,  String pSpeed, Bitmap bitmap){
        // Concatenate the title, description and speed of the post into one variable
        String shareBody = pTitle +"\n"+ pDescription + "\n" + pSpeed;

        // Define the uri for the bitmap
        Uri uri = saveRouteToShare(bitmap);


        // Share intent, brings up the standard menu for user to choose what platform they wish to
        // share their session on
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));




    }

    // This method handles where the bitmap of the route should be saved in order
    // for it to be shared externally
    private Uri saveRouteToShare(Bitmap bitmap) {
        // Define a new file called images
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs(); // create folder if necessary
            File file = new File(imageFolder, "shared_image.png"); // Directory within where the image will be saved

            // Instantiate a FileOutputStream
            FileOutputStream stream = new FileOutputStream(file);
            // Compress the bitmap into PNG format
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            // Assign the uri of the saved image to the variable uri
            uri = FileProvider.getUriForFile(context, "com.example.sailing_tracker.fileprovider", file);

        } catch (Exception e){
            // Get error message in case of exception
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Return the uri to be used in sharePost method
        return uri;
    }

    // This method is responsible for for changing the look of the like button
    // When the like button is no clicked it will have different colour to when it is
    // This helps user know whether they have liked a post or not
    private void setLikes(final MyHolder holder, final String postKey) {
        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUID)){
                    // The user has already liked the post the value should be set to liked
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0,0,0);
                    holder.likeBtn.setText("Liked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // The user has not liked the post, the value should be set to default
                holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0,0,0);
                holder.likeBtn.setText("Like");

            }
        });
    }


    // Method used to reset the recycler view within each post.
    @Override
    public void onViewRecycled(MyHolder holder)
    {

        if (holder.mapCurrent != null)
        {
            holder.mapCurrent.clear();
            holder.mapCurrent.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }


    // Return the length of postList
    @Override
    public int getItemCount() {
       return postList.size();
    }



    // View holder class
    public static class MyHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        // Views from row_post.xml
        ImageView uPictureIv;
        TextView uEmailTv, pTitleTv, pDescriptionTv, pLikesTv, pTimeTv, pAverageSpeedTv;
        Button likeBtn, shareBtn;
        MapView mapView;
        GoogleMap mapCurrent;
        // Define a new arraylist to hold the lat and long data of each post
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        double lat, lon;
        String sessionID;




        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // Init views and assign to variables
            uPictureIv = itemView.findViewById(R.id.pImageIv);
            uEmailTv = itemView.findViewById(R.id.uEmailTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            mapView = itemView.findViewById(R.id.postMap);
            pAverageSpeedTv = itemView.findViewById(R.id.pAverageSpeed);

            if (mapView != null)
            {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }


        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onMapReady(GoogleMap googleMap) {


            // Instantiate the firebase database reference
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // Instantiate google map
            mapCurrent = googleMap;


                // Query which searches for only session data which is within the node of the current sessionID
                // of the postList
                Query getLatLongQuery = mDatabase.child("Sessions").child(sessionID).child("LatLngData");

                getLatLongQuery.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onDataChange(@org.jetbrains.annotations.NotNull @NotNull DataSnapshot dataSnapshot) {
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
                                latLngArrayList.add(new LatLng(lat, lon));
                                // Move the camera to the last coordinate of the session
                                mapCurrent.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 11));
                            }
                        }
                        // Init Polyline options
                        PolylineOptions polylineOptions = new PolylineOptions();
                        // Create polyline options with existing LatLng ArrayList
                        // and configure color and width
                        polylineOptions.addAll(latLngArrayList);
                        polylineOptions
                                .width(5)
                                .color(Color.RED);

                        // Adding multiple points in map using polyline and ArrayList
                        mapCurrent.addPolyline(polylineOptions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Toast to user showing database error
                        Log.e(TAG, "onCancelled: " + error.getMessage());

                    }
                });


            }
        }


    }







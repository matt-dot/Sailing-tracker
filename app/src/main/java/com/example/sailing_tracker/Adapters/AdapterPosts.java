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
import static com.example.sailing_tracker.HomeFragment.sessionIDForPath;


public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;
    View view;


    private final DatabaseReference likesReference;
    private final DatabaseReference postsReference;

    boolean mProcessLike = false;


    String myUID;


    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;

        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesReference = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");


    }
    public void receiveSessionID(String receivedSessionID) {
        // Assign the sessionID from RecordFragment to variable
        // This will be used for database path
        sessionIDForPath = receivedSessionID;

        // Log to make sure the session ID matches the session ID in
        // generated in record fragment
        Log.d("SessionIdCheck", "Session: " + sessionIDForPath);

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


        // Get data
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



        holder.pLikesTv.setText(pLikes + " Likes");
        holder.pAverageSpeedTv.setText(pSpeed);
        holder.uEmailTv.setText(uEmail);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.sessionID = pSessionID;
        Log.d("Time", "onBindViewHolder: " + pTime);
        setLikes(holder, pSessionID);

        // Set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_user_image).into(holder.uPictureIv);
        } catch (Exception e){

        }




        // Handle button clicks
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt((postList.get(position).getpLikes()));
                mProcessLike = true;
                final String postID = postList.get(position).getpSessionID();
                likesReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            if (!snapshot.child(postID).hasChild(myUID)) {
                                postsReference.child(postID).child("pLikes").setValue("" + (pLikes + 1));
                                likesReference.child(postID).child(myUID).setValue("Liked");
                                mProcessLike = false;
                            } else {
                                // already likes this
                                postsReference.child(postID).child("pLikes").setValue("" + (pLikes - 1));
                                likesReference.child(postID).child(myUID).removeValue();
                                mProcessLike = false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();

                View contentView =  view.findViewById(R.id.postMap);
                contentView.setDrawingCacheEnabled(true);
                Bitmap bitmap = contentView.getDrawingCache();


                sharePost(pTitle, pSpeed, pDescription, bitmap);

            }
        });



        GoogleMap thisMap = holder.mapCurrent;




    }


    private void sharePost(String pTitle, String pDescription,  String pSpeed, Bitmap bitmap){

        String shareBody = pTitle +"\n"+ pDescription + "\n" + pSpeed;

        Uri uri = saveRouteToShare(bitmap);


        // share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));




    }

    private Uri saveRouteToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs(); // create folder
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.example.sailing_tracker.fileprovider", file);

        } catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void setLikes(final MyHolder holder, final String postKey) {
        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUID)){
                    // has liked this post
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0,0,0);
                    holder.likeBtn.setText("Liked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0,0,0);
                holder.likeBtn.setText("Like");

            }
        });
    }

    @Override
    public void onViewRecycled(MyHolder holder)
    {
        // Cleanup MapView here?
        if (holder.mapCurrent != null)
        {
            holder.mapCurrent.clear();
            holder.mapCurrent.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }



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
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
        double lat, lon;
        String sessionID;




        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // Init views
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







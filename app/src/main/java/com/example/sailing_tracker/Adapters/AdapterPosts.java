package com.example.sailing_tracker.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Models.ModelPost;
import com.example.sailing_tracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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




    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;


    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_post.xml
        view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);




    }



    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // Get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescription();
        String pTimeStamp = postList.get(position).getpTime();
        Log.i("TAG", "onBindViewHolder: "+pTimeStamp);







        // Covert timestamp into dd/mm//yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();






        holder.uEmailTv.setText(uEmail);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        Log.d("Time", "onBindViewHolder: " + pTimeStamp);

        // Set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_user_image).into(holder.uPictureIv);
        } catch (Exception e){

        }




        // Handle button clicks
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });



        GoogleMap thisMap = holder.mapCurrent;




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
        TextView uEmailTv, pTitleTv, pDescriptionTv, pLikesTv, pTimeTv;
        Button likeBtn, commentBtn, shareBtn;
        MapView mapView;
        GoogleMap mapCurrent;
        private DatabaseReference mDatabase;
        ArrayList<ArrayList<LatLng>> latLngArrayList = new ArrayList<ArrayList<LatLng>>();
        double lat, lon;




        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // Init views
            uPictureIv = itemView.findViewById(R.id.pImageIv);
            uEmailTv = itemView.findViewById(R.id.uEmailTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            mapView = itemView.findViewById(R.id.postMap);


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
            mDatabase = FirebaseDatabase.getInstance().getReference();

            // Instantiate google map
            mapCurrent = googleMap;



            // Path where to save data to
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
                    Log.e(TAG, "onCancelled: "+error.getMessage());

                }
            });
        }
    }
}




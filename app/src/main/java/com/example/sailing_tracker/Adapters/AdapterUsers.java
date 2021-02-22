package com.example.sailing_tracker.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Models.ModelUser;
import com.example.sailing_tracker.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    // Declare the list to hold the model classes
    private final List<ModelUser> userList;


    // This method inflates the value of row_users and returns the holder view
    @NonNull
    @Override
    public AdapterUsers.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_user.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // Data from userList is extracted and assigned to variables
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();
        String userBoatClass = userList.get(position).getBoatClass();


        // The data retrieved from the postList list and assigned to variables
        // then populates the attributes of the row_user
        // They are assigned below
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        holder.mBoatClassTv.setText(userBoatClass);

        // Try catch block used to load the display image of each user
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_user_image)
                    .into(holder.mUsersIv);
        } catch (Exception e) {}




    }

    // Constructor
    public AdapterUsers(List<ModelUser> userList) {
        this.userList = userList;
    }

    // Returns the length of userList
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // View holder class
    static class MyHolder extends RecyclerView.ViewHolder {
        // Define variables
        ImageView mUsersIv;
        TextView mNameTv, mEmailTv, mBoatClassTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            // Assign views to variables
            mUsersIv = itemView.findViewById(R.id.usersIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mBoatClassTv = itemView.findViewById(R.id.boatClassTv);

        }
    }


}
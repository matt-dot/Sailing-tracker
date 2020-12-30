package com.example.sailing_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    private final List<ModelUser> userList;



    // View holder class
    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView mUsersIv;
        TextView mNameTv, mEmailTv, mBoatClassTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            // Init views
            mUsersIv = itemView.findViewById(R.id.usersIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mBoatClassTv = itemView.findViewById(R.id.boatClassTv);
        }
    }



    @NonNull
    @Override
    public AdapterUsers.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_user.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // Get data
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();
        String userBoatClass = userList.get(position).getBoatClass();

        // Set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        holder.mBoatClassTv.setText(userBoatClass);

        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_user_image)
                    .into(holder.mUsersIv);
        } catch (Exception e) {}




    }

    // Constructor
    AdapterUsers(List<ModelUser> userList) {
        this.userList = userList;






    }






    @Override
    public int getItemCount() {
        return userList.size();
    }


}
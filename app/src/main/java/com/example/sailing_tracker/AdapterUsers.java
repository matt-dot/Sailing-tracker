package com.example.sailing_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    List<ModelUser> userList;
    Context context;



    @NonNull
    @Override
    public AdapterUsers.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    // Constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUsers.MyHolder holder, int position) {
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
        } catch( Exception e ) {

        }

        // Handle item click

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        });





    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // View holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mUsersIv;
        TextView mNameTv, mEmailTv, mBoatClassTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            // Init views
            mUsersIv = itemView.findViewById(R.id.usersIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mBoatClassTv = itemView.findViewById(R.id.boatClassTv);
        }
    }
}

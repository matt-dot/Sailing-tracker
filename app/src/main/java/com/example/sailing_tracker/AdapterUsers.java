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

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> userList;

    // Constructor
    public AdapterUsers(Context context, List<ModelUser> userList){
        this.context = context;
        this.userList = userList;
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        // Get data
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        final String userEmail = userList.get(i).getEmail();
        String userBoatClass = userList.get(i).getBoatClass();


        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        myHolder.mBoatClassTv.setText(userBoatClass);

        try{
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_user_image)
                    .into(myHolder.mUsersIv);


        }
        catch(Exception e){


        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        ImageView mUsersIv;
        TextView mNameTv, mEmailTv, mBoatClassTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mUsersIv = itemView.findViewById(R.id.usersIv);
            mNameTv = itemView.findViewById(R.id.person_NameTv);
            mEmailTv = itemView.findViewById(R.id.person_EmailTv);
            mBoatClassTv = itemView.findViewById(R.id.person_boatClassTv);



        }
    }
}

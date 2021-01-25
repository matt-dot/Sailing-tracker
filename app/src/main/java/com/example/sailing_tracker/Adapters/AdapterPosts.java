package com.example.sailing_tracker.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sailing_tracker.Models.ModelPost;
import com.example.sailing_tracker.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);


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


        // Covert timestamp into dd/mm//yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();



        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.uNameTv.setText(uName);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);





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




    }

    @Override
    public int getItemCount() {
       return postList.size();
    }

    // View holder class
    static class MyHolder extends RecyclerView.ViewHolder{

        // Views from row_post.xml
        ImageView uPictureIv;
        TextView uNameTv, pTitleTv, pDescriptionTv, pLikesTv, pTimeTv;
        Button likeBtn, commentBtn, shareBtn;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // Init views
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
        }
    }
}

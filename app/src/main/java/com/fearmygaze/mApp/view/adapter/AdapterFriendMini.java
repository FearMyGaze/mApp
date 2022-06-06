package com.fearmygaze.mApp.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Friend;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterFriendMini extends RecyclerView.Adapter<AdapterFriendMini.MyViewHolder> {

    List<Friend> friendList;
    Activity activity;

    public AdapterFriendMini(List<Friend> friendList, Activity activity) {
        this.friendList = friendList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_mini, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String image = friendList.get(position).getImage();
        String username = friendList.get(position).getUsername();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);
        
        holder.username.setText(username);
        
        holder.frameLayout.setOnClickListener(v -> {
            Toast.makeText(activity, "Clicked !", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        
        FrameLayout frameLayout;
        ShapeableImageView image;
        MaterialTextView username;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.adapterFriendMiniRoot);
            image = itemView.findViewById(R.id.adapterFriendMiniImage);
            username = itemView.findViewById(R.id.adapterFriendMiniUsername);
        }
    }
}

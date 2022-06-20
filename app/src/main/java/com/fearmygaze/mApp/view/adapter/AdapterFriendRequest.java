package com.fearmygaze.mApp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.FriendRequest;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterFriendRequest extends RecyclerView.Adapter<AdapterFriendRequest.MyViewHolder> {

    List<FriendRequest> friendRequestList;

    public AdapterFriendRequest(List<FriendRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    @NonNull
    @Override
    public AdapterFriendRequest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFriendRequest.MyViewHolder holder, int position) {

        String image = friendRequestList.get(position).getImage();
        String username = friendRequestList.get(position).getUsername();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);

        holder.username.setText(username);

        holder.accept.setOnClickListener(v -> { //TODO: Add Accept Func
            Toast.makeText(v.getContext(), "Friend Accepted", Toast.LENGTH_SHORT).show();
        });

        holder.decline.setOnClickListener(v -> { // TODO: Add Decline Func
            Toast.makeText(v.getContext(), "Friend Declined", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView image;
        MaterialTextView username;
        ImageButton accept, decline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.adapterFriendRequestImage);
            username = itemView.findViewById(R.id.adapterFriendRequestUsername);
            accept = itemView.findViewById(R.id.adapterFriendRequestAccept);
            decline = itemView.findViewById(R.id.adapterFriendRequestDecline);
        }
    }
}

package com.fearmygaze.mApp.view.adapter;

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

public class AdapterFriendList extends RecyclerView.Adapter<AdapterFriendList.MyViewHolder>{

    List<Friend> friendList;

    public AdapterFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public AdapterFriendList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterFriendList.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFriendList.MyViewHolder holder, int position) {
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
            Toast.makeText(v.getContext(), "This will Create a conversation and open chat activity", Toast.LENGTH_SHORT).show();
        });

        holder.frameLayout.setOnLongClickListener(v -> {
            Toast.makeText(v.getContext(), "This will open a bottomSheetDialog with choices", Toast.LENGTH_SHORT).show();
            return true;
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
            frameLayout = itemView.findViewById(R.id.adapterFriendListRoot);
            image = itemView.findViewById(R.id.adapterFriendListImage);
            username = itemView.findViewById(R.id.adapterFriendListUsername);
        }
    }
}

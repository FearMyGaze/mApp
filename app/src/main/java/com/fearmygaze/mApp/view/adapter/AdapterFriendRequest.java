package com.fearmygaze.mApp.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IFriendRequestAdapter;
import com.fearmygaze.mApp.model.FriendRequest;
import com.fearmygaze.mApp.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterFriendRequest extends RecyclerView.Adapter<AdapterFriendRequest.MyViewHolder> {

    List<FriendRequest> friendRequestList;
    User user;
    private final IFriendRequestAdapter iRecycler;
    private int offset;

    public AdapterFriendRequest(List<FriendRequest> friendRequestList, User user, IFriendRequestAdapter iRecycler) {
        this.friendRequestList = friendRequestList;
        this.user = user;
        this.iRecycler = iRecycler;
        this.offset = 0;
    }

    @NonNull
    @Override
    public AdapterFriendRequest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_request, parent, false), iRecycler);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFriendRequest.MyViewHolder holder, int position) {
        String image = friendRequestList.get(position).getImage();
        String username = friendRequestList.get(position).getUsername();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.overrideOf(70, 70))
                .into(holder.image);

        holder.username.setText(username);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refillList(List<FriendRequest> friendRequests) {
        this.friendRequestList = friendRequests;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addResultAndRefreshAdapter(List<FriendRequest> friendRequests) {
        this.friendRequestList.addAll(friendRequests);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearListAndRefreshAdapter() {
        this.friendRequestList.clear();
        notifyDataSetChanged();
    }

    public void removeItemAndRefresh(int position){
        this.friendRequestList.remove(position);
        notifyItemRemoved(position);
    }

    public int getFriendID(int pos){
        return friendRequestList.get(pos).getId();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        MaterialTextView username;
        ImageButton accept, decline;

        public MyViewHolder(@NonNull View itemView, IFriendRequestAdapter iRecycler) {
            super(itemView);
            image = itemView.findViewById(R.id.adapterFriendRequestImage);
            username = itemView.findViewById(R.id.adapterFriendRequestUsername);
            accept = itemView.findViewById(R.id.adapterFriendRequestAccept);
            decline = itemView.findViewById(R.id.adapterFriendRequestDecline);

            accept.setOnClickListener(v -> {
                if (iRecycler != null){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        iRecycler.onItemClickAccept(pos);
                    }
                }
            });

            decline.setOnClickListener(v -> {
                if (iRecycler != null){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        iRecycler.onItemCLickIgnore(pos);
                    }
                }
            });

        }
    }
}
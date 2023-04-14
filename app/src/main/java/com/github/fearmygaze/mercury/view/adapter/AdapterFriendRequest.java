package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterFriendRequest extends RecyclerView.Adapter<AdapterFriendRequest.FriendRequestVH> {

    List<User> users;
    String userID;
    boolean showIgnoreButton;

    public AdapterFriendRequest(List<User> userList, String userID, boolean showIgnoreButton) {
        this.users = userList;
        this.userID = userID;
        this.showIgnoreButton = showIgnoreButton;
    }

    @NonNull
    @Override
    public FriendRequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendRequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestVH holder, int position) {
        Glide.with(holder.itemView.getRootView()).load(users.get(position).imageURL).centerInside().into(holder.image); //TODO: Add Placeholder
        holder.name.setText(users.get(position).name);
        holder.username.setText(users.get(position).username);
        holder.ignore.setOnClickListener(v ->
                Friends.ignoreRequest(userID, users.get(position).userUID, new Friends.OnResultListener() {
                    @Override
                    public void onResult(int result) {
                        if (result == 1) {
                            removeSpecificUser(holder.getAbsoluteAdapterPosition());
                        } else {
                            Toast.makeText(holder.ignore.getContext(), v.getResources().getString(R.string.adapterFriendRequest), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(holder.ignore.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
        holder.accept.setOnClickListener(v ->
                Friends.acceptRequest(userID, users.get(position).userUID, new Friends.OnResultListener() {
                    @Override
                    public void onResult(int result) {
                        if (result == 1) {
                            removeSpecificUser(holder.getAbsoluteAdapterPosition());
                        } else {
                            Toast.makeText(holder.accept.getContext(), v.getResources().getString(R.string.adapterFriendRequest), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(holder.accept.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
        holder.root.setOnClickListener(v ->
                v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                        .putExtra("senderID", userID)
                        .putExtra("receiverID", users.get(position).userUID)
                        .putExtra("enableButton", true))
        );
        holder.more.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
        });
        if (showIgnoreButton) holder.ignore.setVisibility(View.VISIBLE);
        else holder.ignore.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> list) {
        users = list;
        notifyItemRangeChanged(0, users.size());
    }

    public void removeSpecificUser(int pos) {
        notifyItemRemoved(pos);
        users.remove(pos);
    }

    protected static class FriendRequestVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image, more;
        TextView name, username;
        MaterialButton ignore, accept;

        public FriendRequestVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterFriendRequestRoot);
            image = itemView.findViewById(R.id.adapterFriendRequestImage);
            name = itemView.findViewById(R.id.adapterFriendRequestName);
            username = itemView.findViewById(R.id.adapterFriendRequestUsername);
            more = itemView.findViewById(R.id.adapterFriendRequestOptions);
            ignore = itemView.findViewById(R.id.adapterFriendRequestIgnore);
            accept = itemView.findViewById(R.id.adapterFriendRequestAccept);
        }
    }
}

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
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.activity.ChatRoom;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Iterator;
import java.util.List;

public class AdapterUserList extends RecyclerView.Adapter<AdapterUserList.UserVH> {

    List<User> users;
    String userID;
    boolean showProfile;

    public AdapterUserList(List<User> users, String userID, boolean showProfile) {
        this.users = users;
        this.userID = userID;
        this.showProfile = showProfile;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        Glide.with(holder.itemView.getRootView()).load(users.get(position).imageURL).centerInside().into(holder.image); //TODO: Add Placeholder
        holder.name.setText(users.get(position).name);
        holder.username.setText(users.get(position).username);
        holder.root.setOnClickListener(v -> {
            if (!showProfile) {
                v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                        .putExtra("senderID", userID)
                        .putExtra("receiverID", users.get(position).userUID)
                        .putExtra("showFriends", users.get(position).showFriends)
                );
            } else { //TODO: We need to check if the chatRoomExists and if the room doesnt exist pass the following
                v.getContext().startActivity(new Intent(v.getContext(), ChatRoom.class)
                        .putExtra("receiverID", users.get(position).userUID)
                        .putExtra("userImage", users.get(position).imageURL)
                        .putExtra("userName", users.get(position).name));
            }
        });
        holder.more.setOnClickListener(v -> {//TODO: show extra stuff for the user
            Toast.makeText(v.getContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
        });
    }

    public void setUsers(List<User> list) {
        users = list;
        notifyItemRangeChanged(0, users.size());
    }

    public void clearUsers() {
        notifyItemRangeRemoved(0, users.size());
        users.clear();
    }

    public void filterUsers(String name) {
        Iterator<User> iterator = users.iterator();
        if (name.startsWith("@")) {
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (!user.username.startsWith("@" + name)) {
                    iterator.remove();
                    notifyItemRemoved(users.indexOf(user));
                }
            }
        } else {
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (!user.name.startsWith(name)) {
                    iterator.remove();
                    notifyItemRemoved(users.indexOf(user));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    protected static class UserVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image, more;
        TextView name, username;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserListRoot);
            image = itemView.findViewById(R.id.adapterUserListImage);
            name = itemView.findViewById(R.id.adapterUserListName);
            more = itemView.findViewById(R.id.adapterUserListOptions);
            username = itemView.findViewById(R.id.adapterUserListUsername);
        }
    }
}

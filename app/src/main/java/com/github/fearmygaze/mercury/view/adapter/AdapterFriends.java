package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class AdapterFriends extends FirestoreRecyclerAdapter<Request, AdapterFriends.FriendsVH> {

    User user;

    public AdapterFriends(User user, @NonNull FirestoreRecyclerOptions<Request> options) {
        super(options);
        this.user = user;
    }

    @NonNull
    @Override
    public FriendsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_friends, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsVH holder, int position, @NonNull Request model) {
        Profile profile = model.getSenderProfile();
        Tools.profileImage(profile.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(profile.getUsername());
        holder.root.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Now we create a room if not exists", Toast.LENGTH_SHORT).show();
        });
        holder.root.setOnLongClickListener(v -> {

            return true;
        });
    }

    public static class FriendsVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username;

        public FriendsVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserFriendsRoot);
            image = itemView.findViewById(R.id.adapterUserFriendsImage);
            username = itemView.findViewById(R.id.adapterUserFriendsUsername);
        }
    }
}

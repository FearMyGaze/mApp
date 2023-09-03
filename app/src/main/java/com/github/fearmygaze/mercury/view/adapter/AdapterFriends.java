package com.github.fearmygaze.mercury.view.adapter;

import android.content.Context;
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
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

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
        Context context = holder.itemView.getContext();
        if (getItemViewType(holder.getAbsoluteAdapterPosition()) == 0) {
            Tools.profileImage(model.getSenderProfile().getImage(), context).into(holder.image);
            holder.username.setText(model.getSenderProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                Auth.getUserProfile(model.getSenderProfile().getId(), context, new OnUserResponseListener() {
                    @Override
                    public void onSuccess(int code, User requested) {
                        if (code == 0) {
                            Tools.goToProfileViewer(user, requested, context);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            holder.root.setOnLongClickListener(v -> {
                Toast.makeText(v.getContext(), "Add action", Toast.LENGTH_SHORT).show();
                return true;
            });
        } else {
            Tools.profileImage(model.getReceiverProfile().getImage(), context).into(holder.image);
            holder.username.setText(model.getReceiverProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                Auth.getUserProfile(model.getReceiverProfile().getId(), context, new OnUserResponseListener() {
                    @Override
                    public void onSuccess(int code, User requested) {
                        if (code == 0) {
                            Tools.goToProfileViewer(user, requested, context);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            holder.root.setOnLongClickListener(v -> {
                Toast.makeText(v.getContext(), "Add action", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(getItem(position).getReceiver(), user.getId()) ? 0 : 1;
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

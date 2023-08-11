package com.github.fearmygaze.mercury.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class AdapterBlocked extends FirestorePagingAdapter<Request, AdapterBlocked.BlockedVH> {

    User user;
    RecyclerView recyclerView;
    Activity activity;

    public AdapterBlocked(User user, @NonNull FirestorePagingOptions<Request> options, RecyclerView recyclerView, Activity activity) {
        super(options);
        this.user = user;
        this.recyclerView = recyclerView;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BlockedVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BlockedVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_block, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BlockedVH holder, int position, @NonNull Request request) {
        //We only pass the Receiver values because we search as sender
        Tools.profileImage(request.getReceiverImage(), recyclerView.getContext()).into(holder.image);
        holder.username.setText(request.getReceiverUsername());
        holder.root.setOnClickListener(v -> Auth
                .getUserProfile(request.getReceiverID(), v.getContext(), new OnUserResponseListener() {
                    @Override
                    public void onSuccess(int code, User requestedUser) {
                        if (code == 0) {
                            Tools.goToProfileViewer(user.getId(), requestedUser, v.getContext());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
        holder.unBlock.setOnClickListener(v -> Friends
                .unBlock(request, v.getContext(), new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                        } else {
                            Toast.makeText(v.getContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    protected static class BlockedVH extends RecyclerView.ViewHolder {
        MaterialCardView root, unBlock;
        ShapeableImageView image;
        TextView username;

        public BlockedVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserBlockedRoot);
            image = itemView.findViewById(R.id.adapterUserBlockedImage);
            username = itemView.findViewById(R.id.adapterUserBlockedUsername);
            unBlock = itemView.findViewById(R.id.adapterUserBlockedUnBlock);
        }
    }
}

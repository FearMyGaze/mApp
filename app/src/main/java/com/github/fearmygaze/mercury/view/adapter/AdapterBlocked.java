package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.Search;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class AdapterBlocked extends FirestorePagingAdapter<Request, AdapterBlocked.BlockedVH> {

    User user;
    RecyclerView recyclerView;

    public AdapterBlocked(User user, @NonNull FirestorePagingOptions<Request> options, RecyclerView recyclerView) {
        super(options);
        this.user = user;
        this.recyclerView = recyclerView;
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @NonNull
    @Override
    public BlockedVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BlockedVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_block, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BlockedVH holder, int position, @NonNull Request request) {
        //We only pass the Receiver values because we search as sender
        Profile profile = request.getReceiverProfile();
        Tools.profileImage(profile.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(profile.getUsername());
        holder.root.setOnClickListener(v -> {
            new Search(v.getContext()).getUserById(profile.getId(), new CallBackResponse<User1>() {
                @Override
                public void onSuccess(User1 fetched) {
                    v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                            .putExtra(User.PARCEL, user)
                            .putExtra(User.PARCEL_OTHER, fetched));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.unBlock.setOnClickListener(v -> {
            RequestActions actions = new RequestActions(v.getContext());
            actions.unBlock(request.getId(), new CallBackResponse<String>() {
                @Override
                public void onSuccess(String object) {
                    notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
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

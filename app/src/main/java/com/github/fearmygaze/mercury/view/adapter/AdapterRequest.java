package com.github.fearmygaze.mercury.view.adapter;

import android.app.Activity;
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
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class AdapterRequest extends FirestorePagingAdapter<Request, AdapterRequest.RequestVH> {

    User user;
    RecyclerView recyclerView;
    Activity activity;

    public AdapterRequest(User user, @NonNull FirestorePagingOptions<Request> options, RecyclerView recyclerView, Activity activity) {
        super(options);
        this.user = user;
        this.recyclerView = recyclerView;
        this.activity = activity;
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @NonNull
    @Override
    public AdapterRequest.RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_request, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterRequest.RequestVH holder, int position, @NonNull Request request) {
        //We only pass the Sender values because we search as receiver
        Tools.profileImage(request.getSenderImage(), recyclerView.getContext()).into(holder.image);
        holder.username.setText(request.getSenderUsername());
        holder.root.setOnClickListener(v -> {
            Auth.getUserProfile(request.getSenderID(), v.getContext(), new OnUserResponseListener() {
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
            });
        });
        holder.accept.setOnClickListener(v -> {
            Friends.answerRequest(request, Friends.OPTION_ACCEPT, v.getContext(), new OnResponseListener() {
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
            });
        });
        holder.remove.setOnClickListener(v -> {
            Friends.answerRequest(request, Friends.OPTION_REMOVE, v.getContext(), new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    if (code == 0) {
                        notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(v.getContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    protected static class RequestVH extends RecyclerView.ViewHolder {
        MaterialCardView root, accept, remove;
        ShapeableImageView image;
        TextView username;

        public RequestVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserRequestRoot);
            image = itemView.findViewById(R.id.adapterUserRequestImage);
            username = itemView.findViewById(R.id.adapterUserRequestUsername);
            accept = itemView.findViewById(R.id.adapterUserRequestAccept);
            remove = itemView.findViewById(R.id.adapterUserRequestDismiss);
        }
    }
}

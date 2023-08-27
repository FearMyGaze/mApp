package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterPending extends RecyclerView.Adapter<AdapterPending.PendingVH> {

    User user;
    List<Request> requests;

    public AdapterPending(User user, List<Request> requests) {
        this.user = user;
        this.requests = requests;
    }

    @NonNull
    @Override
    public PendingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PendingVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_pending, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PendingVH holder, int position) {
        //We only pass the Sender values because we search as receiver
        Request request = requests.get(holder.getAbsoluteAdapterPosition());
        Profile profile = requests.get(holder.getAbsoluteAdapterPosition()).getSenderProfile();
        Tools.profileImage(profile.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(profile.getUsername());
        holder.root.setOnClickListener(v -> {
            Auth.getUserProfile(profile.getId(), v.getContext(), new OnUserResponseListener() {
                @Override
                public void onSuccess(int code, User requestedUser) {
                    if (code == 0) {
                        Tools.goToProfileViewer(user, requestedUser, v.getContext());
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
                        clear(holder.getAbsoluteAdapterPosition());
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
                        clear(holder.getAbsoluteAdapterPosition());
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

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void set(List<Request> list) {
        requests = list;
        notifyItemRangeChanged(0, list.size());
    }

    public void clear(int pos) {
        requests.remove(pos);
        notifyItemRemoved(pos);
    }

    public void clear() {
        notifyItemRangeRemoved(0, requests.size());
        requests.clear();
    }

    protected static class PendingVH extends RecyclerView.ViewHolder {
        MaterialCardView root, accept, remove;
        ShapeableImageView image;
        TextView username;

        public PendingVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserRequestRoot);
            image = itemView.findViewById(R.id.adapterUserRequestImage);
            username = itemView.findViewById(R.id.adapterUserRequestUsername);
            accept = itemView.findViewById(R.id.adapterUserRequestAccept);
            remove = itemView.findViewById(R.id.adapterUserRequestDismiss);
        }
    }
}

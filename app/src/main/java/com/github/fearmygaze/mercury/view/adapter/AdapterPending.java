package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
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
            new UserActions(v.getContext()).getUserByID(profile.getId(), new CallBackResponse<User>() {
                @Override
                public void onSuccess(User object) {
                    v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                            .putExtra(User.PARCEL, user)
                            .putExtra(User.PARCEL_OTHER, object));
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
        holder.accept.setOnClickListener(v -> {
            new RequestActions(v.getContext())
                    .accept(request.getId(), new CallBackResponse<String>() {
                        @Override
                        public void onSuccess(String object) {
                            clear(holder.getAbsoluteAdapterPosition());
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
        holder.remove.setOnClickListener(v -> {
            new RequestActions(v.getContext())
                    .delete(request.getId(), new CallBackResponse<String>() {
                        @Override
                        public void onSuccess(String object) {
                            clear(holder.getAbsoluteAdapterPosition());
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

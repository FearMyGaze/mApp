package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
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
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class AdapterFriends extends FirestoreRecyclerAdapter<Request, AdapterFriends.FriendsVH> {

    User ourUser, otherUser;
    private final SimpleInterface listener;

    public AdapterFriends(User ourUser, User otherUser, @NonNull FirestoreRecyclerOptions<Request> options, SimpleInterface listener) {
        super(options);
        this.ourUser = ourUser;
        this.otherUser = otherUser;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_friends, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsVH holder, int position, @NonNull Request model) {
        listener.count(getItemCount());
        if (getItemViewType(holder.getAbsoluteAdapterPosition()) == 0) {
            Tools.profileImage(model.getSenderProfile().getImage(), holder.itemView.getContext()).into(holder.image);
            holder.username.setText(model.getSenderProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                new UserActions(v.getContext()).getUserByID(model.getSenderProfile().getId(), new CallBackResponse<User>() {
                    @Override
                    public void onSuccess(User object) {
                        AppDatabase.getInstance(holder.itemView.getContext())
                                .cachedProfile()
                                .insert(new Profile(object.getId(), object.getUsername(), object.getImage()));
                        v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                                .putExtra(User.PARCEL, ourUser)
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
        } else {
            Tools.profileImage(model.getReceiverProfile().getImage(), holder.itemView.getContext()).into(holder.image);
            holder.username.setText(model.getReceiverProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                new UserActions(v.getContext()).getUserByID(model.getReceiverProfile().getId(), new CallBackResponse<User>() {
                    @Override
                    public void onSuccess(User object) {
                        AppDatabase.getInstance(holder.itemView.getContext())
                                .cachedProfile()
                                .insert(new Profile(object.getId(), object.getUsername(), object.getImage()));
                        v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                                .putExtra(User.PARCEL, ourUser)
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
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(getItem(position).getReceiver(), otherUser.getId()) ? 0 : 1;
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

    public interface SimpleInterface {
        void count(int count);
    }
}

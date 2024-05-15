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
import com.github.fearmygaze.mercury.database.RoomDB;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterFriendsRoom extends FirestoreRecyclerAdapter<Request, AdapterFriendsRoom.FriendsRoomVH> {

    User user;
    List<Profile> profiles = new ArrayList<>();
    private final SimpleInterface listener;

    public AdapterFriendsRoom(User user, @NonNull FirestoreRecyclerOptions<Request> options, SimpleInterface listener) {
        super(options);
        this.user = user;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendsRoomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsRoomVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_friends, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsRoomVH holder, int position, @NonNull Request model) {
        listener.itemCounter(getItemCount());
        if (getItemViewType(holder.getAbsoluteAdapterPosition()) == 0) {
            Tools.profileImage(model.getSenderProfile().getImage(), holder.itemView.getContext()).into(holder.image);
            holder.username.setText(model.getSenderProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                holder.root.setChecked(!holder.root.isChecked());
                if (holder.root.isChecked()) {
                    addProfile(model.getSenderProfile());
                    listener.selectedUsers(profiles.size());
                } else {
                    removeProfile(model.getSenderProfile());
                    listener.selectedUsers(profiles.size());
                }
            });
            holder.root.setOnLongClickListener(v -> {
                new Search(v.getContext()).getUserById(model.getSenderProfile().getId(), new CallBackResponse<User1>() {
                    @Override
                    public void onSuccess(User1 fetched) {
                        RoomDB.getInstance(v.getContext()).profiles().insert(user.getId(), fetched);
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
                return true;
            });
        } else {
            Tools.profileImage(model.getReceiverProfile().getImage(), holder.itemView.getContext()).into(holder.image);
            holder.username.setText(model.getReceiverProfile().getUsername());
            holder.root.setOnClickListener(v -> {
                holder.root.setChecked(!holder.root.isChecked());
                if (holder.root.isChecked()) {
                    addProfile(model.getReceiverProfile());
                    listener.selectedUsers(profiles.size());
                } else {
                    removeProfile(model.getReceiverProfile());
                    listener.selectedUsers(profiles.size());
                }
            });
            holder.root.setOnLongClickListener(v -> {
                new Search(v.getContext()).getUserById(model.getReceiverProfile().getId(), new CallBackResponse<User1>() {
                    @Override
                    public void onSuccess(User1 fetched) {
                        RoomDB.getInstance(v.getContext()).profiles().insert(user.getId(), fetched);
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
                return true;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(getItem(position).getReceiver(), user.getId()) ? 0 : 1;
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile);
    }

    public List<Profile> getSelectedProfiles() {
        return profiles;
    }

    public void clearSelectedProfiles() {
        profiles.clear();
    }

    public static class FriendsRoomVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username;

        public FriendsRoomVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserFriendsRoot);
            image = itemView.findViewById(R.id.adapterUserFriendsImage);
            username = itemView.findViewById(R.id.adapterUserFriendsUsername);
        }
    }

    public interface SimpleInterface {
        void itemCounter(int count);

        void selectedUsers(int count);
    }
}

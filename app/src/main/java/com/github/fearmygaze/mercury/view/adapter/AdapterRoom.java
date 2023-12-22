package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class AdapterRoom extends FirestoreRecyclerAdapter<Room, RecyclerView.ViewHolder> {

    User user;
    RecyclerView recyclerView;

    public AdapterRoom(User user, FirestoreRecyclerOptions<Room> options, RecyclerView recyclerView) {
        super(options);
        this.user = user;
        this.recyclerView = recyclerView;
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new RoomRegularVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_room_regular, parent, false));
        } else {
            return new RoomGroupVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_room_group, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Room room) {
        if (getItemViewType(holder.getAbsoluteAdapterPosition()) == 0) {
            RoomRegularVH roomRegularVH = (RoomRegularVH) holder;
            Tools.profileImage(Room.getProfileImages(user, room).get(0).getImage(), recyclerView.getContext()).into(roomRegularVH.image);
            roomRegularVH.name.setText(Room.showName(user, room));
            roomRegularVH.message.setText("send the first message");
            roomRegularVH.time.setText("11:10");
            roomRegularVH.root.setOnClickListener(v -> Tools.goToChat(user, room, v.getContext()));
        } else {
            RoomGroupVH roomGroupVH = (RoomGroupVH) holder;
            Tools.profileImage(Room.getProfileImages(user, room).get(0).getImage(), recyclerView.getContext()).into(roomGroupVH.firstImage);
            Tools.profileImage(Room.getProfileImages(user, room).get(1).getImage(), recyclerView.getContext()).into(roomGroupVH.secondImage);
            roomGroupVH.name.setText(Room.showName(user, room));
            roomGroupVH.message.setText("send the first message");
            roomGroupVH.time.setText("11:10"); //TODO: Fix
            roomGroupVH.root.setOnClickListener(v -> Tools.goToChat(user, room, v.getContext()));
        }
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex);
        switch (type) {
            case ADDED:
                notifyItemInserted(newIndex);
                break;
            case CHANGED:
                Objects.requireNonNull(this.recyclerView.getItemAnimator()).setChangeDuration(0);
                Objects.requireNonNull(this.recyclerView.getItemAnimator()).setMoveDuration(150);
                notifyItemChanged(newIndex);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(getItem(position).getRoomType(), Room.RoomType.Group) ? 1 : 0;
    }

    public static class RoomRegularVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView name, message, time;

        protected RoomRegularVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterRoomRegularRoot);
            image = itemView.findViewById(R.id.adapterRoomRegularImage);
            name = itemView.findViewById(R.id.adapterRoomRegularName);
            message = itemView.findViewById(R.id.adapterRoomRegularMessage);
            time = itemView.findViewById(R.id.adapterRoomRegularTime);
        }
    }

    public static class RoomGroupVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView firstImage, secondImage;
        TextView name, message, time;

        protected RoomGroupVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterRoomGroupRoot);
            firstImage = itemView.findViewById(R.id.adapterRoomGroupFirstImage);
            secondImage = itemView.findViewById(R.id.adapterRoomGroupSecondImage);
            name = itemView.findViewById(R.id.adapterRoomGroupName);
            message = itemView.findViewById(R.id.adapterRoomGroupMessage);
            time = itemView.findViewById(R.id.adapterRoomGroupTime);
        }
    }
}

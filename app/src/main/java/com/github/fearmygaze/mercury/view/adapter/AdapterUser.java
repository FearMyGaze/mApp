package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_REQUESTS = 0, TYPE_SEARCH = 1, TYPE_BLOCKED = 2, TYPE_ROOM = 3;

    List<User> users;
    String id;
    int type;

    private OnCounterListener listener;

    public AdapterUser(List<User> users, String id, @IntRange(from = 0, to = 3) int type) {
        this.users = users;
        this.id = id;
        this.type = type;
    }

    public AdapterUser(List<User> users, String id, @IntRange(from = 0, to = 3) int type, OnCounterListener listener) {
        this.users = users;
        this.id = id;
        this.type = type;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type) {
            case 1:
                return new UserSearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_search, parent, false));
            case 2:
                return new UserBlockedVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_block, parent, false));
            case 3:
                return new UserRoomVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_room, parent, false));
            default:
                return new UserRequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_pending, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (type) {
            case 1:
                UserSearchVH searchVH = (UserSearchVH) holder;
                Tools.profileImage(users.get(position).getImage(), searchVH.image.getContext()).into(searchVH.image);
                searchVH.username.setText(users.get(position).getUsername());
                searchVH.status.setText(users.get(position).getStatus());
//                searchVH.root.setOnClickListener(v -> Tools.goToProfileViewer(id, users.get(holder.getAbsoluteAdapterPosition()), v.getContext()));
                break;
            case 2:
                UserBlockedVH blockedVH = (UserBlockedVH) holder;
                Tools.profileImage(users.get(position).getImage(), blockedVH.image.getContext()).into(blockedVH.image);
                blockedVH.username.setText(users.get(position).getUsername());
//                blockedVH.status.setText(users.get(position).getStatus());
//                blockedVH.root.setOnClickListener(v -> Tools.goToProfileViewer(id, users.get(holder.getAbsoluteAdapterPosition()), v.getContext()));
                blockedVH.unBlock.setOnClickListener(v -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                    builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                            .setTitle("Unblock" + users.get(position).getUsername() + "?")
                            .setMessage("They will be able to follow you")
                            .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                            .setPositiveButton(R.string.generalConfirm, (dialog, i) ->{
//                                Friends.unBlock(id, users.get(position).getId(), v.getContext(), new OnResponseListener() {
//                                    @Override
//                                    public void onSuccess(int code) {
//                                        if (code == 0) {
//                                            Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
//                                            clearUser(holder.getAbsoluteAdapterPosition());
//                                        } else {
//                                            Toast.makeText(v.getContext(), "An Error has occurred", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(String message) {
//                                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                            })
                            .show();
                });
                break;
            case 3:
                UserRoomVH roomVH = (UserRoomVH) holder;
                Tools.profileImage(users.get(position).getImage(), roomVH.image.getContext()).into(roomVH.image);
                roomVH.username.setText(users.get(position).getUsername());
                roomVH.status.setText(users.get(position).getStatus());
                roomVH.root.setOnClickListener(v -> {
                    roomVH.root.setChecked(!roomVH.root.isChecked());
                    if (roomVH.root.isChecked()) {
                        users.get(holder.getAbsoluteAdapterPosition()).setSelected(true);
                        listener.count(getSelectedUsers().size());
                    } else {
                        users.get(holder.getAbsoluteAdapterPosition()).setSelected(false);
                        listener.count(getSelectedUsers().size());
                    }
                });
                roomVH.root.setOnLongClickListener(v -> {
//                    Tools.goToProfileViewer(id, users.get(holder.getAbsoluteAdapterPosition()), v.getContext());
                    return true;
                });
                break;
            default:
                UserRequestVH requestVH = (UserRequestVH) holder;
                Tools.profileImage(users.get(position).getImage(), requestVH.image.getContext()).into(requestVH.image);
                requestVH.username.setText(users.get(position).getUsername());
                requestVH.status.setText(users.get(position).getStatus());
//                requestVH.root.setOnClickListener(v -> Tools.goToProfileViewer(id, users.get(holder.getAbsoluteAdapterPosition()), v.getContext()));
                requestVH.accept.setOnClickListener(v -> {
//                    Friends.answerRequest(id, users.get(position).getId(), Friends.OPTION_ACCEPT, v.getContext(), new OnResponseListener() {
//                        @Override
//                        public void onSuccess(int code) {
//                            if (code == 0) {
//                                Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
//                                clearUser(holder.getAbsoluteAdapterPosition());
//                            } else
//                                Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(String message) {
//                            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                });
                requestVH.dismiss.setOnClickListener(v -> {
//                    Friends.answerRequest(id, users.get(position).getId(), Friends.OPTION_ACCEPT, v.getContext(), new OnResponseListener() {
//                        @Override
//                        public void onSuccess(int code) {
//                            if (code == 0) {
//                                Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
//                                clearUser(holder.getAbsoluteAdapterPosition());
//                            } else
//                                Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(String message) {
//                            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                });
                break;
        }
    }

    public void setData(List<User> list) {
        users = list;
        notifyItemRangeChanged(0, users.size());
    }

    public void clearData() {
        notifyItemRangeRemoved(0, users.size());
        users.clear();
    }

    public void clearUser(int position) {
        notifyItemRemoved(position);
        users.remove(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public Map<String, Boolean> getSelectedUsers() {
        Map<String, Boolean> map = new HashMap<>();
        for (User user : users) {
            if (user.isSelected()) {
                map.put(user.getId(), true);
            }
        }
        return map;
    }

    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        for (User user : users) {
            if (user.isSelected()) {
                list.add(user);
            }
        }
        return list;
    }

    public void setFilteredUsers(String name) {
        if (name.length() > 0) {
            Iterator<User> iterator = users.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (!user.getUsername().startsWith(name)) {
                    notifyItemRemoved(users.indexOf(user));
                    iterator.remove();
                    break;
                }
            }
        }
    }

    protected static class UserSearchVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username, status;

        public UserSearchVH(@NonNull View itemView) {
            super(itemView);
//            root = itemView.findViewById(R.id.adapterUserSearchRoot);
//            image = itemView.findViewById(R.id.adapterUserSearchImage);
//            username = itemView.findViewById(R.id.adapterUserSearchUsername);
//            status = itemView.findViewById(R.id.adapterUserSearchStatus);
        }
    }

    protected static class UserRequestVH extends RecyclerView.ViewHolder {
        MaterialCardView root, accept, dismiss;
        ShapeableImageView image;
        TextView username, status;

        public UserRequestVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserRequestRoot);
            image = itemView.findViewById(R.id.adapterUserRequestImage);
            username = itemView.findViewById(R.id.adapterUserRequestUsername);
//            status = itemView.findViewById(R.id.adapterUserRequestStatus);
            accept = itemView.findViewById(R.id.adapterUserRequestAccept);
            dismiss = itemView.findViewById(R.id.adapterUserRequestDismiss);
        }
    }

    protected static class UserBlockedVH extends RecyclerView.ViewHolder {
        MaterialCardView root, unBlock;
        ShapeableImageView image;
        TextView username;

        public UserBlockedVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserBlockedRoot);
            image = itemView.findViewById(R.id.adapterUserBlockedImage);
            username = itemView.findViewById(R.id.adapterUserBlockedUsername);
//            status = itemView.findViewById(R.id.adapterUserBlockedStatus);
            unBlock = itemView.findViewById(R.id.adapterUserBlockedUnBlock);
        }
    }

    protected static class UserRoomVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username, status;

        public UserRoomVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserRoomRoot);
            image = itemView.findViewById(R.id.adapterUserRoomImage);
            username = itemView.findViewById(R.id.adapterUserRoomUsername);
            status = itemView.findViewById(R.id.adapterUserRoomStatus);
        }
    }

    public interface OnCounterListener {
        void count(int count);
    }
}

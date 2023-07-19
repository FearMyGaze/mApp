package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_REQUESTS = 0, TYPE_SEARCH = 1, TYPE_BLOCKED = 2, TYPE_ROOM = 3;

    List<User> users;
    List<User> originalUsers;
    String id;
    boolean showProfile;
    int type;

    private OnRoomListener listener;

    public AdapterUser(List<User> users, String id, @IntRange(from = 0, to = 3) int type) {
        this.users = users;
        this.id = id;
        this.type = type;
    }

    public AdapterUser(List<User> users, String id, @IntRange(from = 0, to = 3) int type, OnRoomListener listener) {
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
                return new UserRequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_request, parent, false));
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
                searchVH.root.setOnClickListener(v -> v.getContext()
                        .startActivity(new Intent(v.getContext(), ProfileViewer.class)
                                .putExtra(User.ID, id)
                                .putExtra("userData", users.get(holder.getAbsoluteAdapterPosition())))
                );
                break;
            case 2:
                UserBlockedVH blockedVH = (UserBlockedVH) holder;
                Tools.profileImage(users.get(position).getImage(), blockedVH.image.getContext()).into(blockedVH.image);
                blockedVH.username.setText(users.get(position).getUsername());
                blockedVH.status.setText(users.get(position).getStatus());
                blockedVH.root.setOnClickListener(v -> v.getContext()
                        .startActivity(new Intent(v.getContext(), ProfileViewer.class)
                                .putExtra(User.ID, id)
                                .putExtra("userData", users.get(holder.getAbsoluteAdapterPosition())))
                );
                blockedVH.unBlock.setOnClickListener(v -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                    builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                            .setTitle("Unblock" + users.get(position).getUsername() + "?")
                            .setMessage("They will be able to follow you")
                            .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                            .setPositiveButton(R.string.generalConfirm, (dialog, i) ->
                                    Friends.removeBlock(id, users.get(position).getId(), v.getContext(), new OnResponseListener() {
                                        @Override
                                        public void onSuccess(int code) {
                                            if (code == 0) {
                                                Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                clearUser(holder.getAbsoluteAdapterPosition());
                                            } else {
                                                Toast.makeText(v.getContext(), "An Error has occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                                        }
                                    }))
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
                        listener.onAction(getSelectedUsers().size());
                    } else {
                        users.get(holder.getAbsoluteAdapterPosition()).setSelected(false);
                        listener.onAction(getSelectedUsers().size());
                    }
                });
                break;
            default:
                UserRequestVH requestVH = (UserRequestVH) holder;
                Tools.profileImage(users.get(position).getImage(), requestVH.image.getContext()).into(requestVH.image);
                requestVH.username.setText(users.get(position).getUsername());
                requestVH.status.setText(users.get(position).getStatus());
                requestVH.root.setOnClickListener(v -> v.getContext()
                        .startActivity(new Intent(v.getContext(), ProfileViewer.class)
                                .putExtra(User.ID, id)
                                .putExtra("userData", users.get(holder.getAbsoluteAdapterPosition())))
                );
                requestVH.accept.setOnClickListener(v ->
                        Friends.answerRequest(id, users.get(position).getId(), Friends.OPTION_ACCEPT, v.getContext(), new OnResponseListener() {
                            @Override
                            public void onSuccess(int code) {
                                if (code == 0) {
                                    Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                    clearUser(holder.getAbsoluteAdapterPosition());
                                } else
                                    Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        })
                );
                requestVH.dismiss.setOnClickListener(v ->
                        Friends.answerRequest(id, users.get(position).getId(), Friends.OPTION_ACCEPT, v.getContext(), new OnResponseListener() {
                            @Override
                            public void onSuccess(int code) {
                                if (code == 0) {
                                    Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                    clearUser(holder.getAbsoluteAdapterPosition());
                                } else
                                    Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        })
                );
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

//    public void filterUsers(String name) {
//        Iterator<User> iterator = users.iterator();
//        if (name.startsWith("@")) {
//            while (iterator.hasNext()) {
//                User user = iterator.next();
//                if (!user.username.startsWith("@" + name)) {
//                    iterator.remove();
//                    notifyItemRemoved(users.indexOf(user));
//                }
//            }
//        } else {
//            while (iterator.hasNext()) {
//                User user = iterator.next();
//                if (!user.name.startsWith(name)) {
//                    iterator.remove();
//                    notifyItemRemoved(users.indexOf(user));
//                }
//            }
//        }
//    }

    public void filterUsers(@Nullable String text) {
//        if (text == null) {
//            users.clear();
//            users.addAll(originalUsers);
////            notifyItemRangeChanged(0, users.size());
//            notifyDataSetChanged(); // display the full list
//        } else {
//            Iterator<User> iterator = users.iterator();
//            while (iterator.hasNext()) {
//                User user = iterator.next();
//                if (!user.name.startsWith(text)) {
//                    int position = users.indexOf(user);
//                    iterator.remove();
//                    notifyItemRemoved(position); // remove the item from the list
//                }
//            }
//            for (int i = 0; i < originalUsers.size(); i++) {
//                User user = originalUsers.get(i);
//                if (user.name.startsWith(text) && !users.contains(user)) {
//                    users.add(i, user);
//                    notifyItemInserted(i); // add the item to the list
//                }
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public List<String> getSelectedUsers() {
        List<String> selectedUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isSelected()) {
                selectedUsers.add(user.getId());
            }
        }
        return selectedUsers;
    }

    protected static class UserSearchVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username, status;

        public UserSearchVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserSearchRoot);
            image = itemView.findViewById(R.id.adapterUserSearchImage);
            username = itemView.findViewById(R.id.adapterUserSearchUsername);
            status = itemView.findViewById(R.id.adapterUserSearchStatus);
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
            status = itemView.findViewById(R.id.adapterUserRequestStatus);
            accept = itemView.findViewById(R.id.adapterUserRequestAccept);
            dismiss = itemView.findViewById(R.id.adapterUserRequestDismiss);
        }
    }

    protected static class UserBlockedVH extends RecyclerView.ViewHolder {
        MaterialCardView root, unBlock;
        ShapeableImageView image;
        TextView username, status;

        public UserBlockedVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserBlockedRoot);
            image = itemView.findViewById(R.id.adapterUserBlockedImage);
            username = itemView.findViewById(R.id.adapterUserBlockedUsername);
            status = itemView.findViewById(R.id.adapterUserBlockedStatus);
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
}

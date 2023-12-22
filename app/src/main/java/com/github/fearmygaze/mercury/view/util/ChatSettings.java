package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.UIAction;
import com.github.fearmygaze.mercury.firebase.ChatEvents;
import com.github.fearmygaze.mercury.firebase.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.RoomActions;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomResponseListener;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

public class ChatSettings extends AppCompatActivity {

    //TopBar
    ShapeableImageView goBack, report;
    TextView roomName;

    //Extra info
    MaterialCardView members;
    TextView membersValue, createdValue;
    ShapeableImageView roomImage;

    //Update
    MaterialCardView changeName;

    //Danger
    MaterialCardView changeOwner, leaveRoom, deleteRoom;

    Bundle bundle;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        goBack = findViewById(R.id.chatRoomSettingsBack);
        report = findViewById(R.id.chatRoomSettingsReport);
        roomName = findViewById(R.id.chatRoomSettingsRoomName);
        roomImage = findViewById(R.id.chatRoomSettingsRoomImage);

        members = findViewById(R.id.chatRoomSettingsMembers);
        membersValue = findViewById(R.id.chatRoomSettingsMembersValue);
        createdValue = findViewById(R.id.chatRoomSettingsCreatedValue);

        changeName = findViewById(R.id.chatRoomSettingsUpdateRoomName);

        changeOwner = findViewById(R.id.chatRoomSettingsDangerOwner);
        deleteRoom = findViewById(R.id.chatRoomSettingsDangerDeleteRoom);
        leaveRoom = findViewById(R.id.chatRoomSettingsDangerLeaveRoom);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        room = bundle.getParcelable(Room.PARCEL);
        if (user == null || room == null) onBackPressed();

        RoomActions roomDao = new RoomActions(ChatSettings.this);
        setupSettings(user, room);

        goBack.setOnClickListener(v -> onBackPressed());

        report.setOnClickListener(v -> {

        });
        members.setOnClickListener(v -> {

        });
        changeName.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ChatSettings.this);
            if (room.getOwnerID().equals(user.getId())) {
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_change_room_name, null);
                EditText nameEditText = dialogView.findViewById(R.id.dialogChangeRoomNameEditText);
                nameEditText.setText(room.getRoomName());
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setCancelable(false)
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg1))
                        .setView(dialogView)
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalSave, ((dialog, i) -> {
                            String s = nameEditText.getText().toString().trim();
                            if (s.equals(room.getRoomName())) {
                                dialog.dismiss();
                            } else {
                                ChatEvents.updateName(room, s, new OnResponseListener() {
                                    @Override
                                    public void onSuccess(int code) {
                                        if (code == 0) {
                                            UIAction.closeKeyboard(v.getContext());
                                            dialog.dismiss();
                                            Toast.makeText(ChatSettings.this, "Room name updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        dialog.dismiss();
                                        Toast.makeText(ChatSettings.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })).show();
            } else {
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg5))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, ((dialog, i) -> dialog.dismiss()))
                        .show();
            }
        });

        changeOwner.setOnClickListener(v -> {

        });

        leaveRoom.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ChatSettings.this);
            if (room.getOwnerID().equals(user.getId())) {
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg1))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, ((dialog, i) -> dialog.dismiss()))
                        .show();
            } else {
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg3))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, (dialog, i) -> roomDao.leave(room.getRoomID(), Profile.create(user), new CallBackResponse<String>() {
                            @Override
                            public void onSuccess(String object) {
                                getOnBackPressedDispatcher().onBackPressed();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(ChatSettings.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(ChatSettings.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .show();
            }
        });

        deleteRoom.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ChatSettings.this);
            if (room.getOwnerID().equals(user.getId())) {
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg2))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, (dialog, i) -> {
                            roomDao.delete(room.getRoomID(), new CallBackResponse<String>() {
                                @Override
                                public void onSuccess(String object) {
                                    getOnBackPressedDispatcher().onBackPressed();
                                }

                                @Override
                                public void onError(String message) {
                                    this.onFailure(message);
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(ChatSettings.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).show();
            } else {
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(getString(R.string.chatRoomSettingsDialogTitle))
                        .setMessage(getString(R.string.chatRoomSettingsDialogMsg4))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, ((dialog, i) -> dialog.dismiss()))
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatEvents.getRoomSnapshot(room, new OnRoomResponseListener() {
            @Override
            public void onSuccess(int code, Room r) {
                if (code == 0) {
                    if (!r.getVisibleTo().contains(user.getId())) {
                        onBackPressed();
                    } else {
                        room = r;
                        setupSettings(user, room);
                    }
                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(String msg) {
                onBackPressed();
            }
        });
    }

    private void setupSettings(User user, Room room) {
        Tools.profileImage(Room.getProfileImages(user, room).get(0).getImage(), ChatSettings.this).into(roomImage);
        roomName.setText(Room.showName(user, room));
        membersValue.setText(String.valueOf(room.getProfiles().size()));
        createdValue.setText(Room.showDate(room));

        if (!room.getOwnerID().equals(user.getId())) {
            changeName.setVisibility(View.GONE);
            deleteRoom.setVisibility(View.GONE);
        } else {
            changeName.setVisibility(View.VISIBLE);
            deleteRoom.setVisibility(View.VISIBLE);
        }
    }
}

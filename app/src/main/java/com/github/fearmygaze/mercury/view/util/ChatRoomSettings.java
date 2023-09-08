package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class ChatRoomSettings extends AppCompatActivity {

    //TopBar
    MaterialCardView goBack, report, members;
    TextView roomName, membersValue, createdValue;
    ShapeableImageView roomImage;

    //Update
    MaterialCardView changeRoomName, changeRoomImage, addMember, removeMember;

    //Danger
    MaterialCardView changeOwner, deleteRoom;

    Bundle bundle;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_settings);

        goBack = findViewById(R.id.chatRoomSettingsBack);
        report = findViewById(R.id.chatRoomSettingsReport);
        roomName = findViewById(R.id.chatRoomSettingsRoomName);
        roomImage = findViewById(R.id.chatRoomSettingsRoomImage);

        members = findViewById(R.id.chatRoomSettingsMembers);
        membersValue = findViewById(R.id.chatRoomSettingsMembersValue);
        createdValue = findViewById(R.id.chatRoomSettingsCreatedValue);

        changeRoomName = findViewById(R.id.chatRoomSettingsUpdateRoomName);
        changeRoomImage = findViewById(R.id.chatRoomSettingsUpdateRoomImage);
        addMember = findViewById(R.id.chatRoomSettingsUpdateAddMember);
        removeMember = findViewById(R.id.chatRoomSettingsUpdateRemoveMember);

        changeOwner = findViewById(R.id.chatRoomSettingsDangerOwner);
        deleteRoom = findViewById(R.id.chatRoomSettingsDangerDeleteRoom);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();

        user = bundle.getParcelable(User.PARCEL);
        room = bundle.getParcelable("room");

        setUpRoom(room);

        goBack.setOnClickListener(v -> onBackPressed());

        report.setOnClickListener(v -> {
        });

        members.setOnClickListener(v -> {
        });

        changeRoomName.setOnClickListener(v -> {
        });
        changeRoomImage.setOnClickListener(v -> {
        });
        addMember.setOnClickListener(v -> {
        });
        removeMember.setOnClickListener(v -> {
        });
        changeOwner.setOnClickListener(v -> {
        });
        deleteRoom.setOnClickListener(v -> {
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setUpRoom(Room room) {
        Tools.profileImage("default", ChatRoomSettings.this).into(roomImage);
        if (room.getIsGroup()) {
            roomName.setText(room.getName());
        } else roomName.setText(Room.modifyName(user, room));
        membersValue.setText(String.valueOf(room.getMembers().size()));
        createdValue.setText("01/01/1980");
    }
}

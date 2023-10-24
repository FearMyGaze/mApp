package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.ChatEvents;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriendsRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class RoomCreator extends AppCompatActivity {

    ShapeableImageView goBackBtn;
    MaterialButton createBtn;

    //Features: Encryption
    ConstraintLayout encryptionBox;
    SwitchMaterial encryptionSwitch;

    AdapterFriendsRoom adapterFriendsRoom;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendList;

    Bundle bundle;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creator);

        goBackBtn = findViewById(R.id.roomCreatorGoBack);
        createBtn = findViewById(R.id.roomCreatorCreateRoom);
        encryptionBox = findViewById(R.id.roomCreatorEncryptionBox);
        encryptionSwitch = findViewById(R.id.roomCreatorEncryptionSwitch);
        friendList = findViewById(R.id.roomCreatorUsers);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();

        user = bundle.getParcelable(User.PARCEL);
        if (user == null) onBackPressed();

        goBackBtn.setOnClickListener(v -> onBackPressed());

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(user), Request.class)
                .setLifecycleOwner(this)
                .build();

        adapterFriendsRoom = new AdapterFriendsRoom(user, options, new AdapterFriendsRoom.SimpleInterface() {
            @Override
            public void itemCounter(int count) {
                if (count < 1) {//TODO: Show `no friends` with an image
                    Toast.makeText(RoomCreator.this, "No friends", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void selectedUsers(int count) {
                createBtn.setEnabled(count > 0 && count <= 10);
                if (count > 10) {
                    Toast.makeText(RoomCreator.this, getString(R.string.roomCreatorMax), Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendList.setLayoutManager(new CustomLinearLayout(RoomCreator.this, LinearLayoutManager.VERTICAL, false));
        friendList.setAdapter(adapterFriendsRoom);
        friendList.setItemAnimator(null);

        encryptionBox.setOnClickListener(v -> encryptionSwitch.setChecked(!encryptionSwitch.isChecked()));
        encryptionBox.setOnLongClickListener(v -> {
            Toast.makeText(RoomCreator.this, getString(R.string.roomCreatorEncryptionHelper), Toast.LENGTH_LONG).show();
            return true;
        });

        createBtn.setOnClickListener(v -> {
            Room.RoomType type = Room.RoomType.Private;
            if (adapterFriendsRoom.getSelectedProfiles().size() > 1) {
                type = Room.RoomType.Group;
            }
            ChatEvents.existingRoom(type, encryptionSwitch.isChecked(), user, adapterFriendsRoom.getSelectedProfiles(),
                    RoomCreator.this, new OnRoomResponseListener() {
                        @Override
                        public void onSuccess(int code, Room room) {
                            if (code == 0) {
                                Toast.makeText(RoomCreator.this, getString(R.string.roomCreatorMessage), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else if (code == 1) {
                                finish();
                                startActivity(new Intent(RoomCreator.this, Chat.class)
                                        .putExtra(User.PARCEL, user)
                                        .putExtra(Room.PARCEL, room));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            Toast.makeText(RoomCreator.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

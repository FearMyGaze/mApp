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
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.firebase.RoomActions;
import com.github.fearmygaze.mercury.model.Profile;
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
    RequestActions requestActions;
    RoomActions roomActions;

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

        requestActions = new RequestActions(this);
        roomActions = new RoomActions(this);
        goBackBtn.setOnClickListener(v -> onBackPressed());

        adapterFriendsRoom = new AdapterFriendsRoom(user,
                new FirestoreRecyclerOptions.Builder<Request>()
                        .setQuery(requestActions.friends(user.getId()), Request.class)
                        .setLifecycleOwner(this)
                        .build(),
                new AdapterFriendsRoom.SimpleInterface() {
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
            roomActions.exists(Profile.create(user), adapterFriendsRoom.getSelectedProfiles(), encryptionSwitch.isChecked(), new CallBackResponse<Room>() {
                @Override
                public void onSuccess(Room room) {
                    startActivity(new Intent(RoomCreator.this, Chat.class)
                            .putExtra(User.PARCEL, user)
                            .putExtra(Room.PARCEL, room));
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(RoomCreator.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(RoomCreator.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

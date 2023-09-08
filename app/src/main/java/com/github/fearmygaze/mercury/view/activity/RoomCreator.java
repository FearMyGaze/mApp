package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriendsRoom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class RoomCreator extends AppCompatActivity {

    MaterialToolbar navigation;
    MaterialButton create;
    TextView selectedUsers;

    AdapterFriendsRoom adapterFriendsRoom;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendList;

    Bundle bundle;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creator);

        navigation = findViewById(R.id.roomCreatorToolBar);
        friendList = findViewById(R.id.roomCreatorUsers);
        create = findViewById(R.id.roomCreatorCreate);
        selectedUsers = findViewById(R.id.roomCreatorSelected);
        selectedUsers.setText(String.format(Locale.getDefault(), "%s %d / %d", getString(R.string.roomCreatorSelected), 0, 10));

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();

        user = bundle.getParcelable(User.PARCEL);

        navigation.setNavigationOnClickListener(v -> onBackPressed());

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(user), Request.class)
                .setLifecycleOwner(this)
                .build();

        adapterFriendsRoom = new AdapterFriendsRoom(user, options, new AdapterFriendsRoom.SimpleInterface() {
            @Override
            public void itemCounter(int count) {
                if (count < 1) {
                    Toast.makeText(RoomCreator.this, "No friends", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void selectedUsers(int count) {
                selectedUsers.setText(String.format(Locale.getDefault(), "%s %d / %d", getString(R.string.roomCreatorSelected), count, 10));
                create.setEnabled(count > 0 && count <= 10);
            }
        });
        friendList.setLayoutManager(new CustomLinearLayout(RoomCreator.this, LinearLayoutManager.VERTICAL, false));
        friendList.setAdapter(adapterFriendsRoom);

        create.setOnClickListener(v -> {
            Log.d("customLog", adapterFriendsRoom.getSelectedProfiles().toString() + "");
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

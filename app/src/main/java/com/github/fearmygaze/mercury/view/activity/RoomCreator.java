package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterUserList;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RoomCreator extends AppCompatActivity {

    ShapeableImageView goBack, group;

    TextInputLayout searchLayout;
    TextInputEditText searchBox;

    AdapterUserList adapterFriendList;
    RecyclerView friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creator);

        goBack = findViewById(R.id.roomCreatorGoBack);
        searchLayout = findViewById(R.id.roomCreatorSearchError);
        group = findViewById(R.id.roomCreatorEnableGroup);
        searchBox = findViewById(R.id.roomCreatorSearch);
        friendList = findViewById(R.id.roomCreatorUsers);

        goBack.setOnClickListener(v -> onBackPressed());
        group.setOnClickListener(v -> {//TODO: switch to groups

        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO: We need to filter the users based on the given name here
//                adapterFriendList.filterUsers(s.toString().trim());
            }
        });

        Friends.friendList(FirebaseAuth.getInstance().getUid(), new Friends.OnExtendedListener() {

            @Override
            public void onResult(int resultCode, List<User> list) {
                if (resultCode == 1) {
                    adapterFriendList.setUsers(list);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(RoomCreator.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        adapterFriendList = new AdapterUserList(new ArrayList<>(), FirebaseAuth.getInstance().getUid(), true);
        friendList.setLayoutManager(new LinearLayoutManager(RoomCreator.this, LinearLayoutManager.VERTICAL, false));
        friendList.setAdapter(adapterFriendList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
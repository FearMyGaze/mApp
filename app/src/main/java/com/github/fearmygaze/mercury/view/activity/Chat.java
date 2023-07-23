package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ChatRoomSettings;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class Chat extends AppCompatActivity {

    //Top Card
    MaterialCardView goBack, cardImage;
    ShapeableImageView roomImage;
    TextView roomName;

    //Center
    SwipeRefreshLayout refresh;
    RecyclerView recycler;

    //Bottom Card
    Group messageOptions;
    MaterialCardView chooseImage, recordVoice, sendMessage;
    EditText message;

    Intent intent;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        goBack = findViewById(R.id.chatRoomGoBack);
        cardImage = findViewById(R.id.chatRoomImage);
        roomImage = findViewById(R.id.chatRoomImageImg);
        roomName = findViewById(R.id.chatRoomName);

        refresh = findViewById(R.id.chatRoomRefresh);
        recycler = findViewById(R.id.chatRoomRecycler);


        message = findViewById(R.id.chatRoomMessage);

        messageOptions = findViewById(R.id.chatRoomBottomCardOptions);
        chooseImage = findViewById(R.id.chatRoomChooseImage);
        recordVoice = findViewById(R.id.chatRoomRecordSound);
        sendMessage = findViewById(R.id.chatRoomSendMessage);

        intent = getIntent();
        user = intent.getExtras().getParcelable("user");
        room = intent.getExtras().getParcelable("room");

        setupRoom(room);

        goBack.setOnClickListener(v -> onBackPressed());

        cardImage.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatRoomSettings.class)
                    .putExtra("user", user)
                    .putExtra("room", room));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        chooseImage.setOnClickListener(v -> {

        });

        recordVoice.setOnClickListener(v -> {

        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    messageOptions.setVisibility(View.GONE);
                } else {
                    messageOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        sendMessage.setOnClickListener(v -> {

        });

        refresh.setOnRefreshListener(() -> refresh.setRefreshing(false));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setupRoom(Room room) {
        if (room.getIsGroup()) {
            roomName.setText(room.getName());
        } else roomName.setText(Room.modifyName(user, room));
        Tools.profileImage("default", Chat.this).into(roomImage);
    }

}

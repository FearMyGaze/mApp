package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.ChatEvents;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRoomResponseListener;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ChatSettings;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class Chat extends AppCompatActivity {

    //Top Card
    ShapeableImageView goBackBtn, settings;
    TextView roomName;

    //Center
    SwipeRefreshLayout refresh;
    RecyclerView recycler;

    //Bottom Card
    Group messageOptions;
    MaterialCardView messageParent, chooseImage, recordVoice, sendMessage;
    EditText message;

    Bundle bundle;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        goBackBtn = findViewById(R.id.chatRoomGoBack);
        settings = findViewById(R.id.chatRoomSettings);
        roomName = findViewById(R.id.chatRoomName);

        refresh = findViewById(R.id.chatRoomRefresh);
        recycler = findViewById(R.id.chatRoomRecycler);

        message = findViewById(R.id.chatRoomMessage);
        messageOptions = findViewById(R.id.chatRoomBottomCardOptions);
        messageParent = findViewById(R.id.chatRoomBottomCard);
        chooseImage = findViewById(R.id.chatRoomChooseImage);
        recordVoice = findViewById(R.id.chatRoomRecordSound);
        sendMessage = findViewById(R.id.chatRoomSendMessage);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        room = bundle.getParcelable(Room.PARCEL);
        if (user == null || room == null) onBackPressed();

        goBackBtn.setOnClickListener(v -> onBackPressed());
        roomName.setText(Room.showName(user, room));
        settings.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatSettings.class)
                    .putExtra(User.PARCEL, user)
                    .putExtra(Room.PARCEL, room));
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
    protected void onResume() {
        super.onResume();
        ChatEvents.getRoomSnapshot(room, new OnRoomResponseListener() {
            @Override
            public void onSuccess(int code, Room r) {
                if (code == 0) {
                    if (!r.getRefers().contains(user.getId())) {
                        onBackPressed();
                    } else {
                        room = r;
                        roomName.setText(Room.showName(user, room));
                    }
                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(String msg) {
                Toast.makeText(Chat.this, "An error occured while", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

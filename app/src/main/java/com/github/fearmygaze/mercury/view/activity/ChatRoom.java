package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class ChatRoom extends AppCompatActivity {

    //Top Card
    MaterialCardView goBack, settings;
    ShapeableImageView userImage;
    TextView userName;

    //Center
    RecyclerView recycler;

    //Bottom Card
    MaterialCardView chooseImage, recordVoice, sendMessage;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        goBack = findViewById(R.id.chatRoomGoBack);
        userImage = findViewById(R.id.chatRoomUserImage);
        userName = findViewById(R.id.chatRoomUserName);
        settings = findViewById(R.id.chatRoomSettings);

        message = findViewById(R.id.chatRoomMessage);

        chooseImage = findViewById(R.id.chatRoomChooseImage);
        recordVoice = findViewById(R.id.chatRoomRecordSound);
        sendMessage = findViewById(R.id.chatRoomSendMessage);

        Glide.with(ChatRoom.this).load(getIntent().getStringExtra("userImage")).centerInside().into(userImage);
        userName.setText(getIntent().getStringExtra("userName"));

        goBack.setOnClickListener(v -> onBackPressed());

        settings.setOnClickListener(v -> {

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
                    chooseImage.setVisibility(View.GONE);
                    recordVoice.setVisibility(View.GONE);
                } else {
                    chooseImage.setVisibility(View.VISIBLE);
                    recordVoice.setVisibility(View.VISIBLE);
                }
            }
        });

        sendMessage.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
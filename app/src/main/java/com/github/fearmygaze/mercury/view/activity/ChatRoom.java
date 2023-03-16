package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.EventNotifier;

public class ChatRoom extends AppCompatActivity {

    RecyclerView recyclerView;

    ImageButton imageButton, sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        recyclerView = findViewById(R.id.chatRoomRecycler);
        imageButton = findViewById(R.id.chatRoomChooseImage);
        sendButton = findViewById(R.id.chatRoomSendMessage);

        sendButton.setOnClickListener(v -> {
            EventNotifier.errorEvent(v,"This is a test", EventNotifier.LENGTH_SHORT);
        });

        imageButton.setOnClickListener(v -> {
            EventNotifier.successEventWithAction(v, "This is a test","UNDO", EventNotifier.LENGTH_INF, new EventNotifier.EventNotifierAction() {
                @Override
                public void onActionClicked() {
                    Toast.makeText(ChatRoom.this, "Action clicked", Toast.LENGTH_LONG).show();
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
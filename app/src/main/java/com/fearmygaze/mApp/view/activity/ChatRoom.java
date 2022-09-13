package com.fearmygaze.mApp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ChatRoom extends AppCompatActivity {

    MaterialToolbar toolbar;

    RecyclerView recyclerView;

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        toolbar = findViewById(R.id.chatRoomToolbar);
        recyclerView = findViewById(R.id.chatRoomRecycler);
        imageButton = findViewById(R.id.chatRoomSendMessage);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(getIntent().getStringExtra("username"));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.chatRoomToolbarItemInfo){
                startActivity(new Intent(ChatRoom.this, ChatRoomSettings.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });

        imageButton.setOnClickListener(v -> {

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
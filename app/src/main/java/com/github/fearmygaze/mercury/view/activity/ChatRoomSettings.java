package com.github.fearmygaze.mercury.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.fearmygaze.mercury.R;

public class ChatRoomSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_settings);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
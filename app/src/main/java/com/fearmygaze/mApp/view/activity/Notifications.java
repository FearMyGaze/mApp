package com.fearmygaze.mApp.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fearmygaze.mApp.R;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
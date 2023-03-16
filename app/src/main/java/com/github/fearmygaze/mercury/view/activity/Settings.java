package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
package com.fearmygaze.mApp.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class Profile extends AppCompatActivity {

    ShapeableImageView userImage;
    MaterialTextView username, userEmail, faq;
    MaterialButton changePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.profileUserImage);
        username = findViewById(R.id.profileUsername);
        userEmail = findViewById(R.id.profileUserEmail);
        changePassword = findViewById(R.id.profileChangePassword);
        faq = findViewById(R.id.profileFAQ);
        
        
        Glide.with(this)
                .load("https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png")
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(userImage);

        changePassword.setOnClickListener(v -> {

        });

        faq.setOnClickListener(v -> {
            Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
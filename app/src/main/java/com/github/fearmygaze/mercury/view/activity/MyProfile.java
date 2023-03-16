package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MyProfile extends AppCompatActivity {

    MaterialCardView profileImageCard;
    ShapeableImageView profileImage;
    TextInputLayout nameError, usernameError, statusError;
    TextInputEditText name, username, status;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        profileImageCard = findViewById(R.id.myProfileImageCard);
        profileImage = findViewById(R.id.myProfileImage);
        nameError = findViewById(R.id.myProfileNameError);
        name = findViewById(R.id.myProfileName);
        usernameError = findViewById(R.id.myProfileUsernameError);
        username = findViewById(R.id.myProfileUsername);
        statusError = findViewById(R.id.myProfileStatusError);
        status = findViewById(R.id.myProfileStatus);

        user = AppDatabase.getInstance(MyProfile.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());

        Glide.with(MyProfile.this).load(user.imageURL).centerInside().into(profileImage);
        name.setText(user.name);
        username.setText(user.username);
        status.setText("Not Yet Implemented");

        name.setEnabled(false);
        username.setEnabled(false);
        status.setEnabled(false);

        profileImageCard.setOnClickListener(v -> {

        });

        nameError.setEndIconOnClickListener(v -> {
            //TODO: When is this clicked return to the previous name if the user didnt click the button
        });

        usernameError.setEndIconOnClickListener(v -> {

        });

        statusError.setEndIconOnClickListener(v -> {

        });
    }
}
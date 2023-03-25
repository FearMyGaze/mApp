package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {

    //User
    ShapeableImageView userImage;
    MaterialButton edit;
    TextView name, username, status;
    ChipGroup chipGroup;

    //Friends
    TextView friendsCounter;
    RecyclerView friendsView;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = AppDatabase.getInstance(Profile.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());

        userImage = findViewById(R.id.profileImage);
        edit = findViewById(R.id.profileEdit);
        name = findViewById(R.id.profileName);
        username = findViewById(R.id.profileUsername);
        status = findViewById(R.id.profileStatus);

        chipGroup = findViewById(R.id.profileExtraInfo);

        friendsCounter = findViewById(R.id.profileFriendsCounter);
        friendsView = findViewById(R.id.profileFriends);

        Glide.with(Profile.this).load(user.imageURL).centerInside().into(userImage);
        name.setText(user.name);
        username.setText(user.username);
        status.setText(user.status);

        userImage.setOnClickListener(v -> {
            //TODO: We need to make a simple imageViewer
        });

        edit.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ProfileEdit.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
        chipGroup.removeAllViews();
        extraInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void extraInfo() {
        if (user.job != null && !user.job.isEmpty()) {
            Chip chip = new Chip(Profile.this);
            chip.setText(user.job);
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_repair_service_24);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }

        if (user.website != null && !user.website.isEmpty()) {
            Chip chip = new Chip(Profile.this);
            chip.setText(Tools.removeHttp(user.website));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setChipIconResource(R.drawable.ic_link_24);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chip.setOnClickListener(v -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(user.website))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            });
            chipGroup.addView(chip);
        }

        if (user.location != null && !user.location.isEmpty()) {
            Chip chip = new Chip(Profile.this);
            chip.setText(user.location);
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_location_24);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }

        if (user.createdAt != null) {
            Chip chip = new Chip(Profile.this);
            chip.setText(String.valueOf(user.createdAt));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipBackgroundColorResource(R.color.basicBackground);
            chipGroup.addView(chip);
        }
    }



}
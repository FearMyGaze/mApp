package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterUserList;
import com.github.fearmygaze.mercury.view.util.ImageViewer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    //User
    ShapeableImageView userImage;
    MaterialButton edit;
    TextView name, username, status;
    ChipGroup chipGroup;

    //Friends
    TextView title, counter;
    AdapterUserList adapterUserList;
    RecyclerView friendsView;

    User user;

    TypedValue typedValue;
    
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

        title = findViewById(R.id.profileFriendsTitle);
        counter = findViewById(R.id.profileFriendsCounter);
        friendsView = findViewById(R.id.profileFriends);

        Glide.with(Profile.this).load(user.imageURL).centerInside().into(userImage);
        name.setText(user.name);
        username.setText(user.username);
        status.setText(user.status);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ImageViewer.class)
                    .putExtra("imageData", user.imageURL)
                    .putExtra("downloadImage", true));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        edit.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ProfileEdit.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Friends.friendList(user.userUID, new Friends.OnDataResultListener() {
            @Override
            public void onResult(int resultCode, List<User> list) {
                if (resultCode == 1) {
                    title.setVisibility(View.VISIBLE);
                    counter.setVisibility(View.VISIBLE);
                    counter.setText(String.valueOf(list.size()));
                    adapterUserList.setUsers(list);
                } else{
                    title.setVisibility(View.GONE);
                    counter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        adapterUserList = new AdapterUserList(new ArrayList<>(), user.userUID, false);
        friendsView.setLayoutManager(new LinearLayoutManager(Profile.this, LinearLayoutManager.VERTICAL, false));
        friendsView.setAdapter(adapterUserList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
        user = AppDatabase.getInstance(Profile.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());
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
        name.setText(user.name);
        status.setText(user.status);
        if (user.job != null && !user.job.isEmpty()) {
            Chip chip = new Chip(Profile.this);
            chip.setText(user.job);
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_repair_service_24);
            chip.setChipIconTintResource(typedValue.resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }

        if (user.website != null && !user.website.isEmpty()) {
            Chip chip = new Chip(Profile.this);
            chip.setText(Tools.removeHttp(user.website));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setChipIconResource(R.drawable.ic_link_24);
            chip.setChipIconTintResource(typedValue.resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
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
            chip.setChipIconTintResource(typedValue.resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }

        if (user.createdAt != null) {
            Chip chip = new Chip(Profile.this);
            chip.setText(String.valueOf(Tools.setDate(user.createdAt)));
            chip.setCheckable(false);
            chip.setChecked(false);
            chip.setClickable(false);
            chip.setChipIconResource(R.drawable.ic_calendar_24);
            chip.setChipIconTintResource(typedValue.resourceId);
            chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
            chipGroup.addView(chip);
        }
    }
}
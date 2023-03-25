package com.github.fearmygaze.mercury.view.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.FriendState;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileViewer extends AppCompatActivity {

    Intent intent;

    ShapeableImageView userImage;
    MaterialButton button;
    TextView username, name, status;
    ChipGroup chipGroup;

    String userUID;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        userImage = findViewById(R.id.profileViewerImage);
        button = findViewById(R.id.profileViewerButton);
        username = findViewById(R.id.profileViewerUsername);
        name = findViewById(R.id.profileViewerName);
        status = findViewById(R.id.profileViewerStatus);
        chipGroup = findViewById(R.id.profileViewerChipGroup);

        intent = getIntent();
        userInfo();

        user = AppDatabase.getInstance(ProfileViewer.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());

        if (user.userUID.equals(intent.getStringExtra("userUID"))) {
            button.setVisibility(View.INVISIBLE);
            button.setClickable(false);
        } else {
            FriendState.areTheyFriends(user.userUID, intent.getStringExtra("userUID"), new FriendState.OnResultListener() {
                @Override
                public void onResult(boolean result) {
                    if (result) button.setText(getString(R.string.generalRemove));
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        button.setOnClickListener(v -> {
            if (button.getText().equals(getString(R.string.generalAdd))) {
                FriendState.sendRequest(user.userUID, intent.getStringExtra("userUID"), new FriendState.OnResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            button.setText(getString(R.string.generalRemove));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                FriendState.removeRequest(user.userUID, intent.getStringExtra("userUID"), new FriendState.OnResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            button.setText(getString(R.string.generalAdd));
                        } else
                            Toast.makeText(ProfileViewer.this, "eixae", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void userInfo() {
        chipGroup.removeAllViews();
        FirebaseDatabase.getInstance().getReference()
                .child("users").orderByChild("name")
                .equalTo(intent.getStringExtra("name")).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                            if (userSnapshot.child("job").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(userSnapshot.child("job").getValue(String.class));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setClickable(false);
                                chip.setChipIconResource(R.drawable.ic_repair_service_24);
                                chip.setChipBackgroundColorResource(R.color.basicBackground);
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("website").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(Tools.removeHttp(Objects.requireNonNull(userSnapshot.child("website").getValue(String.class))));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setChipIconResource(R.drawable.ic_link_24);
                                chip.setChipBackgroundColorResource(R.color.basicBackground);
                                chip.setOnClickListener(v ->
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userSnapshot.child("website").getValue(String.class)))
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("location").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(userSnapshot.child("location").getValue(String.class));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setClickable(false);
                                chip.setChipIconResource(R.drawable.ic_location_24);
                                chip.setChipBackgroundColorResource(R.color.basicBackground);
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("createdAt").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(String.valueOf(userSnapshot.child("createdAt").getValue(Long.class)));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setClickable(false);
                                chip.setChipIconResource(R.drawable.ic_calendar_24);
                                chip.setChipBackgroundColorResource(R.color.basicBackground);
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("status").exists()) {
                                status.setText(userSnapshot.child("status").getValue(String.class));
                            }
                            if (userSnapshot.child("name").exists()) {
                                name.setText(userSnapshot.child("name").getValue(String.class));
                            }
                            if (userSnapshot.child("username").exists()) {
                                username.setText(userSnapshot.child("username").getValue(String.class));
                            }
                            if (userSnapshot.child("imageURL").exists()) {
                                Glide.with(ProfileViewer.this).load(userSnapshot.child("imageURL").getValue(String.class)).centerInside().into(userImage);
                            }
                            if (userSnapshot.child("userUID").exists()) {
                                userUID = userSnapshot.child("userUID").getValue(String.class);
                            }
                        } else {
                            Toast.makeText(ProfileViewer.this, "Error", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileViewer.this, "error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}

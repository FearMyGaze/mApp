package com.github.fearmygaze.mercury.view.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterUserList;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileViewer extends AppCompatActivity {

    Intent intent;

    ShapeableImageView userImage;
    MaterialButton button;
    TextView username, name, status;
    ChipGroup chipGroup;

    AdapterUserList adapterUserList;
    RecyclerView friendsView;

    String senderID, receiverID, imageData;
    boolean showFriends;

    TypedValue typedValue;

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
        friendsView = findViewById(R.id.profileViewerRecycler);

        intent = getIntent();
        senderID = intent.getStringExtra("senderID");
        receiverID = intent.getStringExtra("receiverID");
        showFriends = intent.getBooleanExtra("showFriends", false);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        userInfo();

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(ProfileViewer.this, ImageViewer.class)
                    .putExtra("imageData", imageData)
                    .putExtra("downloadImage", true));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        if (!senderID.equals(receiverID)) {
            button.setVisibility(View.VISIBLE);
            Friends.status(senderID, receiverID, new Friends.OnResultListener() {
                @Override
                public void onResult(int result) {
                    switch (result) {
                        case 2:
                            button.setText("Waiting for response");
                            break;
                        case 1:
                            button.setText("Friends");
                            break;
                        case 0:
                            button.setText("IGNORED");
                            break;
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }

        if (showFriends) {
            Friends.friendList(receiverID, new Friends.OnExtendedListener() {
                @Override
                public void onResult(int resultCode, List<User> list) {
                    if (resultCode == 1) {
                        adapterUserList.setUsers(list);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                }
            });

            adapterUserList = new AdapterUserList(new ArrayList<>(), senderID, false);
            friendsView.setLayoutManager(new LinearLayoutManager(ProfileViewer.this, LinearLayoutManager.VERTICAL, false));
            friendsView.setAdapter(adapterUserList);
        }

        button.setOnClickListener(v -> {
            if (button.getText().equals(getString(R.string.generalAdd))) {
                Friends.sendRequest(senderID, receiverID, new Friends.OnResultListener() {
                    @Override
                    public void onResult(int result) {
                        if (result == 1) {
                            Toast.makeText(ProfileViewer.this, "Request has be Send", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (button.getText().equals("Friends")) {
                Friends.removeFriend(senderID, receiverID, new Friends.OnResultListener() {
                    @Override
                    public void onResult(int result) {
                        if (result == 1) {
                            Toast.makeText(ProfileViewer.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (button.getText().equals("Cancel")) {
                Friends.cancelRequest(senderID, receiverID, new Friends.OnResultListener() {
                    @Override
                    public void onResult(int result) {
                        if (result == 1) {
                            Toast.makeText(ProfileViewer.this, "Request Canceled", Toast.LENGTH_SHORT).show();
                        }
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
                .child("users").orderByChild("userUID")
                .equalTo(receiverID).limitToFirst(1)
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
                                chip.setChipIconTintResource(typedValue.resourceId);
                                chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("website").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(Tools.removeHttp(Objects.requireNonNull(userSnapshot.child("website").getValue(String.class))));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setChipIconResource(R.drawable.ic_link_24);
                                chip.setChipIconTintResource(typedValue.resourceId);
                                chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
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
                                chip.setChipIconTintResource(typedValue.resourceId);
                                chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
                                chipGroup.addView(chip);
                            }
                            if (userSnapshot.child("createdAt").exists()) {
                                Chip chip = new Chip(ProfileViewer.this);
                                chip.setText(String.valueOf(Tools.setDateInProfile(userSnapshot.child("createdAt").getValue(Long.class))));
                                chip.setCheckable(false);
                                chip.setChecked(false);
                                chip.setClickable(false);
                                chip.setChipIconResource(R.drawable.ic_calendar_24);
                                chip.setChipIconTintResource(typedValue.resourceId);
                                chip.setChipBackgroundColorResource(R.color.basicBackgroundAlternate);
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
                                imageData = userSnapshot.child("imageURL").getValue(String.class);
                                Glide.with(ProfileViewer.this).load(imageData).centerInside().into(userImage); //TODO Add placeholder
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

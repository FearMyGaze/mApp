package com.github.fearmygaze.mercury.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    //App Card
    MaterialCardView settingsBtn, notificationsBtn, pendingBtn, ignoredBtn, profileBtn;
    ShapeableImageView profileImage;

    //Actions
    ExtendedFloatingActionButton actions;
    FloatingActionButton personalFab, groupFab, searchFab;
    Group actionGroup;

    //Firebase
    FirebaseUser user;

    //General
    SwipeRefreshLayout refreshLayout;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();

        //General
        refreshLayout = findViewById(R.id.mainSwipeRefresh);

        //App Card
        settingsBtn = findViewById(R.id.mainSettingsButton);
        notificationsBtn = findViewById(R.id.mainNotificationsButton);
        pendingBtn = findViewById(R.id.mainPendingButton);
        ignoredBtn = findViewById(R.id.mainIgnoredButton);
        profileBtn = findViewById(R.id.mainProfileButton);
        profileImage = findViewById(R.id.mainProfileImage);

        //Actions
        actions = findViewById(R.id.mainExtendedFab);
        actions.shrink();
        personalFab = findViewById(R.id.mainPersonalFab);
        groupFab = findViewById(R.id.mainGroupFab);
        searchFab = findViewById(R.id.mainSearchFab);
        actionGroup = findViewById(R.id.mainGroup);

        /*
         * TODO:
         *      When a user scrolls make the extended fab to vanish with animation
         *      Add the Firebase-UI scrollView
         * */

        actions.setOnClickListener(v -> fabController());

        settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        notificationsBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, SignIn.class));
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        });

        pendingBtn.setOnClickListener(v -> {
            List<User> users = AppDatabase.getInstance(Main.this).userDao().getAllUsers();
            for (int i = 0; i < users.size(); i++) {
                Log.d("customLog", users.toString());
            }
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        });

        ignoredBtn.setOnClickListener(v -> {
            AppDatabase.getInstance(Main.this).userDao().deleteAllUsers();
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        });

        profileBtn.setOnClickListener(v -> { //TODO: Delete this Activity(Profile) and make a new (MyProfile)
            startActivity(new Intent(Main.this, MyProfile.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        searchFab.setOnClickListener(v -> {
            fabController();
            startActivity(new Intent(Main.this, Search.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        groupFab.setOnClickListener(v -> {

        });

        personalFab.setOnClickListener(v -> {

        });

        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));
    }

    private void fabController() {
        if (actionGroup.getVisibility() == View.VISIBLE) {
            actionGroup.setVisibility(View.GONE);
            actions.shrink();
        } else {
            actions.extend();
            actionGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) { //TODO: I will have to see the other version on main PC if that is better or not + handle better the errors?
            AppDatabase.getInstance(Main.this).userDao().deleteAllUsers();
            startActivity(new Intent(Main.this, SignIn.class));
            finish();
        } else {
            Glide.with(Main.this).load(user.getPhotoUrl()).centerCrop().apply(new RequestOptions().override(1024)).into(profileImage);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                FirebaseDatabase.getInstance().getReference().child("users")
                        .orderByChild("userUID").equalTo(user.getUid()).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid())
                                            .setValue(new User(user.getUid(), Objects.requireNonNull(user.getEmail()),
                                                    Objects.requireNonNull(userSnapshot.child("username").getValue(String.class)),
                                                    Objects.requireNonNull(user.getDisplayName()),
                                                    userSnapshot.child("imageURL").getValue(String.class), s)
                                                    .toMap()
                                            ).addOnSuccessListener(unused -> {
                                                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                                                .setPhotoUri(Uri.parse(userSnapshot.child("imageURL").getValue(String.class))).build();
                                                        user.updateProfile(changeRequest).addOnSuccessListener(unused1 -> {
                                                            Glide.with(Main.this).load(user.getPhotoUrl()).centerCrop().apply(new RequestOptions().override(1024)).into(profileImage);
                                                            AppDatabase.getInstance(Main.this).userDao().updateUser(new User(user.getUid(), Objects.requireNonNull(user.getEmail()),
                                                                    Objects.requireNonNull(userSnapshot.child("username").getValue(String.class)),
                                                                    Objects.requireNonNull(user.getDisplayName()),
                                                                    userSnapshot.child("imageURL").getValue(String.class), s));
                                                        }).addOnFailureListener(e -> Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                                    }
                                            ).addOnFailureListener(e -> {
                                                Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Main.this, SignIn.class));
                                                finish();
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Main.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Main.this, SignIn.class));
                                finish();
                            }
                        });
            }).addOnFailureListener(e -> {
                Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Main.this, SignIn.class));
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
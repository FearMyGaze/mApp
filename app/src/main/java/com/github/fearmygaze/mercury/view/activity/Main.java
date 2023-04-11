package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {

    //App Card
    MaterialCardView settingsBtn, notificationsBtn, pendingBtn, ignoredBtn, profileBtn;
    ShapeableImageView profileImage;

    //Actions
    ExtendedFloatingActionButton actions;
    FloatingActionButton personalFab, groupFab, searchFab;
    Group actionGroup;

    User user;

    //General
    SwipeRefreshLayout refreshLayout;

    //Firebase
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        auth = FirebaseAuth.getInstance();

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

        setupPreferences();

        /*
         * TODO:
         *      When a user scrolls make the extended fab to vanish with animation
         *      Remove the Group Button and make an option Create Group from the Chat option (?? see if it looks better)
         *      Crash When i search "Lorem1" error message i get is "Inconsistency detected"
         *      Create an imageViewer (with a func to download the image)
         *      I need to find a way to say the user we process the data (in signUp)
         *      Change the naming of the colors
         *      Create a Starting Activity (with the app image and name) with the name Starting and in this
         *          activity update the user components and stuff
         *      For Privacy and Terms i have to create a document in github page and get the link and show it in the app
         *      We need to create a Service that updates the user data
         *      See if we can set the background color inside the theme
         * Color Options:
         *      #FAAB1A, #232F34, #5D1049
         * */

        actions.setOnClickListener(v -> fabController());

        settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        notificationsBtn.setOnClickListener(v -> {
            PrivatePreference preference = new PrivatePreference(Main.this);
            preference.clearAllValues();
        });

        pendingBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, PendingRequests.class)
                    .putExtra("option", "pending")
                    .putExtra("id", user.userUID));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ignoredBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, PendingRequests.class)
                    .putExtra("option", "ignored")
                    .putExtra("id", user.userUID));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        profileBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Profile.class)
                    .putExtra("id", user.userUID));
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
            fabController();
            startActivity(new Intent(Main.this, RoomCreator.class)
                    .putExtra("options", 0));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        setupPreferences();
        Auth.rememberMe(auth, Main.this, new Auth.OnResponseListener() {
            @Override
            public void onResult(int resultCode) {
                switch (resultCode) {
                    case -1:
                    case -2:
                    case 0:
                        startActivity(new Intent(Main.this, SignIn.class));
                        finish();
                        break;
                    case 1:
                        user = AppDatabase.getInstance(Main.this).userDao().getUserByUserUID(auth.getUid());
                        Glide.with(Main.this).load(user.imageURL).fitCenter().into(profileImage);
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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

    private void setupPreferences() {
        if (Tools.getPreference("showIgnored", Main.this)) {
            ignoredBtn.setVisibility(View.VISIBLE);
        } else ignoredBtn.setVisibility(View.GONE);

        if (Tools.getPreference("showPending", Main.this)) {
            pendingBtn.setVisibility(View.VISIBLE);
        } else pendingBtn.setVisibility(View.GONE);
    }

}
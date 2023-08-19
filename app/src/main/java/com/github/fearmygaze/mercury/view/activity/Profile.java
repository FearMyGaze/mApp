package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterUser;
import com.github.fearmygaze.mercury.view.util.ImageViewer;
import com.github.fearmygaze.mercury.view.util.ProfileEdit;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    //User Components
    SwipeRefreshLayout swipe;
    MaterialToolbar toolbar;
    ShapeableImageView userImage;
    MaterialButton edit;
    TextView status;
    ChipGroup chipGroup;

    //Friends
    TextView title, counter;
    AdapterUser adapterUser;
    RecyclerView friendsView;

    User user;

    TypedValue typedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = AppDatabase.getInstance(Profile.this).userDao().getByID(getIntent().getStringExtra(User.ID));
        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        swipe = findViewById(R.id.profileSwipe);
        toolbar = findViewById(R.id.profileToolBar);
        userImage = findViewById(R.id.profileImage);
        edit = findViewById(R.id.profileEdit);
        status = findViewById(R.id.profileStatus);
        chipGroup = findViewById(R.id.profileExtraInfo);

        title = findViewById(R.id.profileFriendsTitle);
        counter = findViewById(R.id.profileFriendsCounter);
        friendsView = findViewById(R.id.profileFriends);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ImageViewer.class)
                    .putExtra("imageData", user.getImage())
                    .putExtra("downloadImage", true));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        edit.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ProfileEdit.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        refreshList(swipe);

        swipe.setOnRefreshListener(() -> refreshList(swipe));

        adapterUser = new AdapterUser(new ArrayList<>(), user.getId(), AdapterUser.TYPE_SEARCH);
        friendsView.setLayoutManager(new LinearLayoutManager(Profile.this, LinearLayoutManager.VERTICAL, false));
        friendsView.setAdapter(adapterUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        } else {
            user = AppDatabase.getInstance(Profile.this).userDao().getByID(FirebaseAuth.getInstance().getUid());
            Tools.profileImage(user.getImage(), Profile.this).into(userImage);
            toolbar.setTitle(user.getUsername());
            status.setText(user.getStatus());
            User.extraInfo(user, true, typedValue.resourceId, chipGroup, Profile.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void refreshList(SwipeRefreshLayout swipe) {

    }
}

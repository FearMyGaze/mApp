package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;
import com.github.fearmygaze.mercury.view.util.ImageViewer;
import com.github.fearmygaze.mercury.view.util.ProfileEdit;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

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
    AdapterFriends adapterFriends;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendsView;

    User user;

    Bundle bundle;
    TypedValue typedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bundle = getIntent().getExtras();

        if (bundle == null) onBackPressed();

        user = bundle.getParcelable("user");
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

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(user), Request.class)
                .setLifecycleOwner(this)
                .build();

        swipe.setOnRefreshListener(() -> refreshList(swipe));

        adapterFriends = new AdapterFriends(user, options);
        friendsView.setLayoutManager(new CustomLinearLayout(Profile.this, LinearLayoutManager.VERTICAL, false));
        friendsView.setAdapter(adapterFriends);
        friendsView.setItemAnimator(null);

        refreshList(swipe);
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
            User.extraInfo(user, typedValue.resourceId, chipGroup, Profile.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void refreshList(SwipeRefreshLayout swipe) {
        adapterFriends.updateOptions(options);
        swipe.setRefreshing(false);
    }
}

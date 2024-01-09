package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.RequestActions;
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

import java.util.Locale;

public class Profile extends AppCompatActivity {

    //User Components
    SwipeRefreshLayout swipe;
    MaterialToolbar toolbar;
    ShapeableImageView userImage, accountType;
    MaterialButton edit;
    TextView status;
    ChipGroup chipGroup;

    //Friends
    TextView title, counter;
    AdapterFriends adapterFriends;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendsRecycler;

    User user;

    Bundle bundle;
    TypedValue typedValue;
    RequestActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bundle = getIntent().getExtras();

        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) onBackPressed();

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        swipe = findViewById(R.id.profileSwipe);
        toolbar = findViewById(R.id.profileToolBar);
        userImage = findViewById(R.id.profileImage);
        accountType = findViewById(R.id.profileAccountType);
        edit = findViewById(R.id.profileEdit);
        status = findViewById(R.id.profileStatus);
        chipGroup = findViewById(R.id.profileExtraInfo);

        title = findViewById(R.id.profileFriendsTitle);
        counter = findViewById(R.id.profileFriendsCounter);
        friendsRecycler = findViewById(R.id.profileFriends);

        actions = new RequestActions(Profile.this);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ImageViewer.class)
                    .putExtra("imageData", user.getImage()));
        });

        edit.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, ProfileEdit.class)
                    .putExtra(User.PARCEL, user));
        });

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(actions.friends(user.getId()), Request.class)
                .setLifecycleOwner(this)
                .build();

        swipe.setOnRefreshListener(() -> refreshList(swipe));

        adapterFriends = new AdapterFriends(user, user, options, count -> {
            if (count > 0) {
                toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), count));
            } else {

            }
        });

        friendsRecycler.setLayoutManager(new CustomLinearLayout(Profile.this, LinearLayoutManager.VERTICAL, false));
        friendsRecycler.setAdapter(adapterFriends);
        friendsRecycler.setItemAnimator(null);

        refreshList(swipe);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            onBackPressed();
        } else {
            Tools.profileImage(user.getImage(), Profile.this).into(userImage);
            if (user.getAccountType() != null && !user.getAccountType().equals("regular")) {
                accountType.setImageDrawable(AppCompatResources.getDrawable(Profile.this, R.drawable.ic_dev_24));
                accountType.setColorFilter(ContextCompat.getColor(this, typedValue.resourceId), PorterDuff.Mode.SRC_IN);
            }
            toolbar.setTitle(user.getUsername());
            status.setText(user.getStatus());
            User.extraInfo(user, typedValue.resourceId, chipGroup, Profile.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void refreshList(SwipeRefreshLayout swipe) {
        adapterFriends.updateOptions(options);
        swipe.setRefreshing(false);
    }
}

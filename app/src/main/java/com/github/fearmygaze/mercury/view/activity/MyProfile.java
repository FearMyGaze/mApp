package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;
import com.github.fearmygaze.mercury.view.util.AccountActions.ProfileEdit;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class MyProfile extends AppCompatActivity {

    //Main Components
    ShapeableImageView goBackBtn, shareBtn, userProfileImage;
    TextView fullName, username, userBio;
    ChipGroup moreUserInfo;
    MaterialButton editProfileBtn;

    //Adapter Components
    TextView friendsCounter;
    AdapterFriends adapterFriends;
    RecyclerView userFriendList;
    FirestoreRecyclerOptions<Request> options;
    ImageView userFriendListError;

    //Bundle
    Bundle bundle;
    User user;

    //Extra
    TypedValue typedValue;
    RequestActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) onBackPressed();

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);

        goBackBtn = findViewById(R.id.myProfileGoBack);
        shareBtn = findViewById(R.id.myProfileShare);
        userProfileImage = findViewById(R.id.myProfileUserImage);
        fullName = findViewById(R.id.myProfileUserFullName);
        username = findViewById(R.id.myProfileUserHandle);
        userBio = findViewById(R.id.myProfileUserBio);
        moreUserInfo = findViewById(R.id.myProfileUserMoreInfo);
        editProfileBtn = findViewById(R.id.myProfileEditProfile);
        friendsCounter = findViewById(R.id.myProfileFriendsCounter);
        userFriendList = findViewById(R.id.myProfileFriendList);
        userFriendListError = findViewById(R.id.myProfileFriendListError);

        actions = new RequestActions(MyProfile.this);
        userBio.setMovementMethod(LinkMovementMethod.getInstance());
        userBio.setHighlightColor(Color.TRANSPARENT);

        goBackBtn.setOnClickListener(v -> onBackPressed());
        shareBtn.setOnClickListener(v -> {
        });

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(actions.friends(user.getId()), Request.class)
                .setLifecycleOwner(this)
                .build();

        adapterFriends = new AdapterFriends(user, user, new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(actions.friends(user.getId()), Request.class)
                .setLifecycleOwner(this)
                .build(),
                count -> {
                    friendsCounter.setText(String.valueOf(count));
                    if (count == 0) {
                        Toast.makeText(MyProfile.this, "Please stop touching grass your allergies will start to ramp up!!", Toast.LENGTH_SHORT).show();
                    }
                });

        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(MyProfile.this, ProfileEdit.class)
                .putExtra(User.PARCEL, user)));

        userFriendList.setLayoutManager(new CustomLinearLayout(MyProfile.this, LinearLayoutManager.VERTICAL, false));
        userFriendList.setAdapter(adapterFriends);
        userFriendList.setItemAnimator(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            onBackPressed();
        } else {
            user = User.getRoomUser(user.getId(), MyProfile.this);
            Tools.profileImage(user.getImage(), MyProfile.this).into(userProfileImage);
            username.setText(user.getUsername());
            userBio.setText(User.formatBio(user.getStatus(), getColor(typedValue.resourceId), text -> {
                if (Patterns.WEB_URL.matcher(text).matches()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.addHttp(user.getWebsite())))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    new UserActions(MyProfile.this).getUserByUsername(text, new CallBackResponse<User>() {
                        @Override
                        public void onSuccess(User otherUser) {
                            startActivity(new Intent(MyProfile.this, ProfileViewer.class)
                                    .putExtra(User.PARCEL, user)
                                    .putExtra(User.PARCEL_OTHER, otherUser));
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(MyProfile.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(MyProfile.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }));
            User.extraInfo(user, typedValue.resourceId, moreUserInfo, MyProfile.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

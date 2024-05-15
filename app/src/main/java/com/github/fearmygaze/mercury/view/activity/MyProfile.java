package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.UIAction;
import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.firebase.Search;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;
import com.github.fearmygaze.mercury.view.util.AccountActions.ProfileEdit;
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
    User1 user;

    //Extra
    TypedValue typedValue;
    RequestActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User1.PARCEL);
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

//        adapterFriends = new AdapterFriends(user, user, new FirestoreRecyclerOptions.Builder<Request>()
//                .setQuery(actions.friends(user.getId()), Request.class)
//                .setLifecycleOwner(this)
//                .build(),
//                count -> {
//                    friendsCounter.setText(String.valueOf(count));
//                    if (count == 0) {
//                        Toast.makeText(MyProfile.this, "Please stop touching grass your allergies will start to ramp up!!", Toast.LENGTH_SHORT).show();
//                    }
//                });

        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(MyProfile.this, ProfileEdit.class)
                .putExtra(User.PARCEL, user)));

//        userFriendList.setLayoutManager(new CustomLinearLayout(MyProfile.this, LinearLayoutManager.VERTICAL, false));
//        userFriendList.setAdapter(adapterFriends);
//        userFriendList.setItemAnimator(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            onBackPressed();
        } else {
            user = RoomDB.getInstance(MyProfile.this).users().getByID(user.getId());
            Tools.profileImage(user.getImage(), MyProfile.this).into(userProfileImage);
            username.setText(user.getUsername());
            if (user.getBio() != null) {
                userBio.setText(User.formatBio(user.getBio(), getColor(typedValue.resourceId), text -> {
                    if (Patterns.WEB_URL.matcher(text).matches()) {
                        UIAction.openToBrowser(MyProfile.this, user.getWebsite());
                    } else {
                        new Search(MyProfile.this).getUserByUsername(text, new CallBackResponse<User1>() {
                            @Override
                            public void onSuccess(User1 otherUser) {
                                UIAction.goToProfileViewer(MyProfile.this, user, otherUser);
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
            }
//            User.extraInfo(user, typedValue.resourceId, moreUserInfo, MyProfile.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

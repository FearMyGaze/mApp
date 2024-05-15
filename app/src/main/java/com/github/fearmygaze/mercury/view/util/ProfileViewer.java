package com.github.fearmygaze.mercury.view.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.RequestActions;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.Search;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileViewer extends AppCompatActivity {

    //Main Components

    ShapeableImageView goBackBtn, blockBtn, reportBtn, userImage;
    TextView userFullName, userHandle, userBio, friendsCounter;
    ChipGroup moreUserInfo;
    MaterialButton requestStateBtn;

    //Adapter Components
    AdapterFriends adapterFriends;
    RecyclerView userFriendList;
    FirestoreRecyclerOptions<Request> options;
    ImageView userFriendListError;

    //Bundle
    Bundle bundle;
    User myUser, visibleUser;
    Request request;

    //Extra
    TypedValue typedValue;
    RequestActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        bundle = getIntent().getExtras();
        if (bundle == null) getOnBackPressedDispatcher().onBackPressed();
        myUser = bundle.getParcelable(User.PARCEL);
        visibleUser = bundle.getParcelable(User.PARCEL_OTHER);
        if (myUser == null || visibleUser == null) getOnBackPressedDispatcher().onBackPressed();

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        goBackBtn = findViewById(R.id.profileViewerGoBack);
        blockBtn = findViewById(R.id.profileViewerBlock);
        reportBtn = findViewById(R.id.profileViewerReport);
        userImage = findViewById(R.id.profileViewerUserImage);
        userFullName = findViewById(R.id.profileViewerFullName);
        userHandle = findViewById(R.id.profileViewerUserHandle);
        userBio = findViewById(R.id.profileViewerUserBio);
        moreUserInfo = findViewById(R.id.profileViewerUserMoreInfo);
        requestStateBtn = findViewById(R.id.profileViewerRequestStatus);
        friendsCounter = findViewById(R.id.profileViewerFriendsCounter);
        userFriendList = findViewById(R.id.profileViewerFriendList);
        userFriendListError = findViewById(R.id.profileViewerFriendListError);

        actions = new RequestActions(ProfileViewer.this);
        userBio.setMovementMethod(LinkMovementMethod.getInstance());
        userBio.setHighlightColor(Color.TRANSPARENT);

        goBackBtn.setOnClickListener(v -> onBackPressed());
        blockBtn.setOnClickListener(v -> {
            if (request.getStatus().equals(Request.RequestStatus.Blocked)) {
                new MaterialAlertDialogBuilder(this)
                        .setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                        .setTitle(String.format("%s %s", "Sorry but you cannot block", visibleUser.getUsername()))
                        .setMessage(String.format("%s %s %s", "Either you or", visibleUser.getUsername(), "issued a block, so you can't block a block"))
                        .setNeutralButton(R.string.generalOK, (dialog, i) -> dialog.dismiss())
                        .show();
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                        .setTitle(String.format("%s %s?", "Block", visibleUser.getUsername()))
                        .setMessage(String.format("%s %s", visibleUser.getUsername(), "will not be able to sent message or be your friend."))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton("Block", (dialog, i) -> {
                            dialog.dismiss();
                            actions.block(Profile.create(myUser), Profile.create(visibleUser),
                                    new CallBackResponse<String>() {
                                        @Override
                                        public void onSuccess(String object) {
                                            requestStateBtn.setText(getString(R.string.requestBlocked));
                                            requestStateBtn.setEnabled(false);
                                            onBackPressed();
                                        }

                                        @Override
                                        public void onError(String message) {
                                            this.onFailure(message);
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }).show();
            }
        });
        reportBtn.setOnClickListener(v -> {
            Toast.makeText(ProfileViewer.this, "Not implemented", Toast.LENGTH_SHORT).show();
        });

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(actions.friends(visibleUser.getId()), Request.class)
                .setLifecycleOwner(this)
                .build();

        updateRequestStatus();
        requestStateBtn.setOnClickListener(v -> requestAction(v.getContext()));

        if (visibleUser.isProfileOpen()) {
            userFriendListError.setVisibility(View.GONE);
            adapterFriends = new AdapterFriends(myUser, visibleUser, options, count -> {
                friendsCounter.setText(String.valueOf(count));
                if (count == 0) {
                    Toast.makeText(ProfileViewer.this, "They currently touching grass", Toast.LENGTH_SHORT).show();
                }
            });
            userFriendList.setLayoutManager(new CustomLinearLayout(ProfileViewer.this, LinearLayoutManager.VERTICAL, false));
            userFriendList.setAdapter(adapterFriends);
            userFriendList.setItemAnimator(null);
        } else {
            friendsCounter.setText("0");
            userFriendListError.setVisibility(View.VISIBLE);
        }
    }

    private void updateRequestStatus() { //TODO: we need the RequestActions to give us the snapshot listener type se we can close it and open it
        if (!myUser.getId().equals(visibleUser.getId())) {
            actions.eventListener(myUser.getId(), visibleUser.getId(), new CallBackResponse<Request>() {
                @Override
                public void onSuccess(Request _request) {
                    request = _request;
                    switch (_request.getStatus()) {
                        case Friends:
                            requestStateBtn.setText(getString(R.string.requestAccepted));
                            requestStateBtn.setEnabled(true);
                            break;
                        case Blocked:
                            requestStateBtn.setText(getString(R.string.requestBlocked));
                            requestStateBtn.setEnabled(false);
                            break;
                        case Waiting:
                            if (_request.getSender().equals(myUser.getId())) {
                                requestStateBtn.setText(getString(R.string.requestWaiting));
                                requestStateBtn.setEnabled(true);
                            } else {
                                requestStateBtn.setText(getString(R.string.requestAnswer));
                                requestStateBtn.setEnabled(true);
                            }
                            break;
                        default:
                            requestStateBtn.setText(getString(R.string.requestNone));
                            requestStateBtn.setEnabled(false);
                    }
                }

                @Override
                public void onError(String message) {
                    requestStateBtn.setText(getString(R.string.requestNone));
                    requestStateBtn.setEnabled(true);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            requestStateBtn.setText("You");
            requestStateBtn.setEnabled(false);
        }
    }

    private void requestAction(Context context) {
        if (request == null) {
            actions.create(Profile.create(myUser), Profile.create(visibleUser), new CallBackResponse<String>() {
                @Override
                public void onSuccess(String object) {
                    requestStateBtn.setText(getString(R.string.requestWaiting));
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            switch (request.getStatus()) {
                case Waiting:
                    if (request.getSender().equals(myUser.getId())) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setBackground(AppCompatResources.getDrawable(context, R.color.basicBackground))
                                .setTitle(String.format("%s %s?", "Do you want to remove the request to", visibleUser.getUsername()))
                                .setMessage("You will still be able to send again later if you changed your mind")
                                .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                                .setPositiveButton("Remove", (dialog, i) -> {
                                    actions.delete(request.getId(), new CallBackResponse<String>() {
                                        @Override
                                        public void onSuccess(String object) {
                                            requestStateBtn.setText(getString(R.string.requestNone));
                                        }

                                        @Override
                                        public void onError(String message) {
                                            this.onFailure(message);
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .show();
                    } else {
                        actions.accept(request.getId(), new CallBackResponse<String>() {
                            @Override
                            public void onSuccess(String object) {
                                requestStateBtn.setText(getString(R.string.generalFriends));
                            }

                            @Override
                            public void onError(String message) {
                                this.onFailure(message);
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case Friends:
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setBackground(AppCompatResources.getDrawable(context, R.color.basicBackground))
                            .setTitle(String.format("%s %s %s?", "Remove", visibleUser.getUsername(), "from friends"))
                            .setMessage("You will not be able to message them but you will still be able to view their profile")
                            .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                            .setPositiveButton("Unfollow", (dialog, i) -> {
                                actions.delete(request.getId(), new CallBackResponse<String>() {
                                    @Override
                                    public void onSuccess(String object) {
                                        requestStateBtn.setText(getString(R.string.requestNone));
                                        updateRequestStatus();
//                                        toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), adapterFriends.getItemCount()));
                                    }

                                    @Override
                                    public void onError(String message) {
                                        this.onFailure(message);
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).show();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }

        Tools.profileImage(visibleUser.getImage(), ProfileViewer.this).into(userImage);
        userHandle.setText(visibleUser.getUsername());
        if (visibleUser.getBio() != null) {
            userBio.setText(User.formatBio(visibleUser.getBio(), getColor(typedValue.resourceId), text -> {
                if (Patterns.WEB_URL.matcher(text).matches()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(User.addHttp(visibleUser.getWebsite())))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    new Search(ProfileViewer.this).getUserByUsername(text, new CallBackResponse<User1>() {
                        @Override
                        public void onSuccess(User1 fetchedUser) {
                            startActivity(new Intent(ProfileViewer.this, ProfileViewer.class)
                                    .putExtra(User.PARCEL, visibleUser)
                                    .putExtra(User.PARCEL_OTHER, fetchedUser));
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }));
        }
        User.extraInfo(visibleUser, typedValue.resourceId, moreUserInfo, ProfileViewer.this);
        requestStateBtn.setEnabled(!myUser.getId().equals(visibleUser.getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

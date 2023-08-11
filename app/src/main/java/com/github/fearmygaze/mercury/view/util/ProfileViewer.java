package com.github.fearmygaze.mercury.view.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterUser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileViewer extends AppCompatActivity {

    Intent intent;

    SwipeRefreshLayout swipeRefreshLayout;

    //TopBar
    MaterialToolbar toolbar;

    ShapeableImageView userImage;
    MaterialButton request;
    TextView status;
    ChipGroup chipGroup;

    AdapterUser adapterUser;
    RecyclerView friendsView;

    TypedValue typedValue;

    //Extra
    Bundle bundle;
    User myUser, otherUser;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        swipeRefreshLayout = findViewById(R.id.profileViewerSwipe);
        toolbar = findViewById(R.id.profileViewerToolBar);

        userImage = findViewById(R.id.profileViewerImage);
        request = findViewById(R.id.profileViewerButton);
        status = findViewById(R.id.profileViewerStatus);
        chipGroup = findViewById(R.id.profileViewerExtraInfo);
        friendsView = findViewById(R.id.profileViewerRecycler);

        intent = getIntent();
        bundle = getIntent().getExtras();

        if (bundle == null && intent.getStringExtra(User.ID) == null) {
            onBackPressed();
        }

        //TODO: Send the whole user and not the id
        myUser = AppDatabase.getInstance(ProfileViewer.this).userDao().getUserByUserID(intent.getStringExtra(User.ID));
        otherUser = bundle.getParcelable("userData");

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        Tools.profileImage(otherUser.getImage(), ProfileViewer.this).into(userImage);
        status.setText(otherUser.getStatus());
        updateStats();
        User.extraInfo(otherUser, false, typedValue.resourceId, chipGroup, ProfileViewer.this);

        if (myUser.getId().equals(otherUser.getId())) {
            request.setEnabled(false);
        }

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(ProfileViewer.this, ImageViewer.class)
                    .putExtra("imageData", otherUser.getImage())
                    .putExtra("downloadImage", true));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(otherUser.getUsername());

//        block.setOnClickListener(v -> {
//            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProfileViewer.this);
//            builder.setBackground(AppCompatResources.getDrawable(ProfileViewer.this, R.color.basicBackground))
//                    .setTitle("Block " + otherUser.getUsername() + "?")
//                    .setMessage(otherUser.getUsername() + " will no longer be able to follow you or message you")
//                    .setPositiveButton(getString(R.string.generalConfirm), (dialog, i) -> {
//                        Friends.block(myUser, otherUser, ProfileViewer.this, new OnResponseListener() {
//                            @Override
//                            public void onSuccess(int code) {
//                                if (code == 0) {
//                                    Toast.makeText(ProfileViewer.this, "User Blocked", Toast.LENGTH_SHORT).show();
//                                    block.setEnabled(false);
//                                    request.setEnabled(false);
//                                } else
//                                    Toast.makeText(ProfileViewer.this, "Error", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onFailure(String message) {
//                                Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    })
//                    .setNegativeButton(getString(R.string.generalCancel), (dialog, i) -> dialog.dismiss())
//                    .show();
//        });
//        report.setOnClickListener(v -> {
//            Toast.makeText(ProfileViewer.this, "Not Implemented", Toast.LENGTH_SHORT).show();
//        });

        request.setOnClickListener(v -> {
            String state = request.getText().toString().trim();
            if (state.equals(getString(R.string.requestAccepted))) {
                Friends.answerRequest(myUser, otherUser, Friends.OPTION_REMOVE, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            request.setText(getString(R.string.requestNone));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } else if (state.equals(getString(R.string.requestWaiting))) {
                Friends.cancelRequest(myUser, otherUser, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            request.setText(getString(R.string.requestNone));
                        } else {
                            Toast.makeText(ProfileViewer.this, "Request changed state", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
            } else if (state.equals(getString(R.string.requestNone))) {
                Friends.sendRequest(myUser, otherUser, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            request.setText(getString(R.string.requestWaiting));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateStats();
            swipeRefreshLayout.setRefreshing(false);
        });
        adapterUser = new AdapterUser(new ArrayList<>(), myUser.getId(), AdapterUser.TYPE_SEARCH);
        friendsView.setLayoutManager(new LinearLayoutManager(ProfileViewer.this, LinearLayoutManager.VERTICAL, false));
        friendsView.setAdapter(adapterUser);
    }

    private void updateStats() {
        Friends.requestStatus(myUser.getId(), otherUser.getId(), ProfileViewer.this, new OnDataResponseListener() {
            @Override
            public void onSuccess(int code, Object data) {
                if (code == 0) {
                    request.setText(data.toString());
                    if (request.getText().equals(getString(R.string.requestBlocked))) {
                        request.setEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
            }
        });

//        Friends.getRequestedList(otherUser, ProfileViewer.this, new OnUsersResponseListener() {
//            @Override
//            public void onSuccess(int code, List<User> list) {
//                if (code == 0 && !list.isEmpty()) {
//                    adapterUser.setData(list);
//                    friendsValue.setVisibility(View.VISIBLE);
//                    friendsValue.setText(getString(R.string.generalFriends) + " " + list.size());
//                } else if (code == 1) {
//                    Toast.makeText(ProfileViewer.this, "Private Profile", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ProfileViewer.this, "0", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(String message) {
//                Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}

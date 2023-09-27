package com.github.fearmygaze.mercury.view.util;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.dao.AuthDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class ProfileViewer extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    MaterialToolbar toolbar;

    ShapeableImageView userImage, accountType;
    MaterialButton request;
    TextView status;
    ChipGroup chipGroup;

    AdapterFriends adapterFriends;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendsView;

    //Extra
    Bundle bundle;
    User myUser, otherUser;
    TypedValue typedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        swipeRefreshLayout = findViewById(R.id.profileViewerSwipe);
        toolbar = findViewById(R.id.profileViewerToolBar);
        userImage = findViewById(R.id.profileViewerImage);
        accountType = findViewById(R.id.profileViewerAccountType);
        request = findViewById(R.id.profileViewerButton);
        status = findViewById(R.id.profileViewerStatus);
        chipGroup = findViewById(R.id.profileViewerExtraInfo);
        friendsView = findViewById(R.id.profileViewerRecycler);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        myUser = bundle.getParcelable(User.PARCEL);
        otherUser = bundle.getParcelable(User.PARCEL_OTHER);
        if (myUser == null || otherUser == null) onBackPressed();
        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(otherUser), Request.class)
                .setLifecycleOwner(this)
                .build();

        Tools.profileImage(otherUser.getImage(), ProfileViewer.this).into(userImage);
        status.setText(otherUser.getStatus());
        if (otherUser.getAccountType() != null && !otherUser.getAccountType().equals("regular")) {
            accountType.setImageDrawable(AppCompatResources.getDrawable(ProfileViewer.this, R.drawable.ic_dev_24));
            accountType.setColorFilter(ContextCompat.getColor(this, typedValue.resourceId), PorterDuff.Mode.SRC_IN);
        }
        updateStats();
        User.extraInfo(otherUser, typedValue.resourceId, chipGroup, ProfileViewer.this);

        if (myUser.getId().equals(otherUser.getId())) {
            request.setEnabled(false);
        }

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(ProfileViewer.this, ImageViewer.class)
                    .putExtra("imageData", otherUser.getImage()));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(otherUser.getUsername());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profileViewerOptionBlock) {
                if (request.getText().toString().equals(getString(R.string.requestBlocked))) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                            .setTitle(String.format("%s %s", "Sorry but you cannot block", otherUser.getUsername()))
                            .setMessage(String.format("%s %s %s", "Either you or", otherUser.getUsername(), "issued a block, so you can't block a block"))
                            .setNegativeButton(R.string.generalOK, (dialog, i) -> dialog.dismiss())
                            .show();
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                            .setTitle(String.format("%s %s?", "Block", otherUser.getUsername()))
                            .setMessage(String.format("%s %s", otherUser.getUsername(), "will not be able to sent message or be your friend."))
                            .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                            .setPositiveButton("Block", (dialog, i) -> {
                                dialog.dismiss();
                                Friends.block(myUser, otherUser, ProfileViewer.this, new OnResponseListener() {
                                    @Override
                                    public void onSuccess(int code) {
                                        if (code == 0) {
                                            request.setText(getString(R.string.requestBlocked));
                                            request.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(ProfileViewer.this, "BLocked", Toast.LENGTH_SHORT).show();
                            }).show();
                }
                return true;
            } else if (item.getItemId() == R.id.profileViewerOptionReport) {

                return true;
            }
            return false;
        });

        request.setOnClickListener(v -> {
            String state = request.getText().toString().trim();
            if (state.equals(getString(R.string.requestAccepted))) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(String.format("%s %s %s?", "Remove", otherUser.getUsername(), "from friends"))
                        .setMessage("You will not be able to message them but you will still be able to view their profile")
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton("Unfollow", (dialog, i) -> {
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
                        }).show();
            } else if (state.equals(getString(R.string.requestWaiting))) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(String.format("%s %s?", "Do you want to remove the request to", otherUser.getUsername()))
                        .setMessage("You will still be able to send again later if you changed your mind")
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton("Remove", (dialog, i) -> {
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
                        }).show();
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
            } else if (state.equals(getString(R.string.requestAnswer))) {
                Friends.answerRequest(myUser, otherUser, Friends.OPTION_ACCEPT, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            request.setText(getString(R.string.generalFriends));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateStats();
            swipeRefreshLayout.setRefreshing(false);
        });

        if (otherUser.isProfileOpen()) {
            adapterFriends = new AdapterFriends(otherUser, options, count ->
                    toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), count)));
            friendsView.setLayoutManager(new CustomLinearLayout(ProfileViewer.this, LinearLayoutManager.VERTICAL, false));
            friendsView.setAdapter(adapterFriends);
            friendsView.setItemAnimator(null);
        } else {
            toolbar.setSubtitle(getString(R.string.profileViewerPrivateProfile));
        }
    }

    private void updateStats() {
        Friends.getStatus(myUser, otherUser, ProfileViewer.this, new OnDataResponseListener() {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AuthDao.getUser() == null) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}

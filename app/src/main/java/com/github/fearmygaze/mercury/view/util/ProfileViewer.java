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
import com.github.fearmygaze.mercury.firebase.RequestEvents;
import com.github.fearmygaze.mercury.firebase.dao.AuthEventsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnRequestResponseListener;
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
    MaterialButton requestStatus;
    TextView status;
    ChipGroup chipGroup;

    AdapterFriends adapterFriends;
    FirestoreRecyclerOptions<Request> options;
    RecyclerView friendsView;

    //Extra
    Bundle bundle;
    User myUser, visibleUser;
    Request request;
    TypedValue typedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        swipeRefreshLayout = findViewById(R.id.profileViewerSwipe);
        toolbar = findViewById(R.id.profileViewerToolBar);
        userImage = findViewById(R.id.profileViewerImage);
        accountType = findViewById(R.id.profileViewerAccountType);
        requestStatus = findViewById(R.id.profileViewerButton);
        status = findViewById(R.id.profileViewerStatus);
        chipGroup = findViewById(R.id.profileViewerExtraInfo);
        friendsView = findViewById(R.id.profileViewerRecycler);

        bundle = getIntent().getExtras();
        if (bundle == null) getOnBackPressedDispatcher().onBackPressed();
        myUser = bundle.getParcelable(User.PARCEL);
        visibleUser = bundle.getParcelable(User.PARCEL_OTHER);
        if (myUser == null || visibleUser == null) getOnBackPressedDispatcher().onBackPressed();
        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(RequestEvents.friendsQuery(visibleUser), Request.class)
                .setLifecycleOwner(this)
                .build();

        Tools.profileImage(visibleUser.getImage(), ProfileViewer.this).into(userImage);
        status.setText(visibleUser.getStatus());
        User.extraInfo(visibleUser, typedValue.resourceId, chipGroup, ProfileViewer.this);
        requestStatus.setEnabled(!myUser.getId().equals(visibleUser.getId()));

        if (visibleUser.getAccountType() != null && !visibleUser.getAccountType().equals("regular")) {
            accountType.setImageDrawable(AppCompatResources.getDrawable(ProfileViewer.this, R.drawable.ic_dev_24));
            accountType.setColorFilter(ContextCompat.getColor(this, typedValue.resourceId), PorterDuff.Mode.SRC_IN);
        }

        updateStats();

        userImage.setOnClickListener(v -> {
            startActivity(new Intent(ProfileViewer.this, ImageViewer.class)
                    .putExtra("imageData", visibleUser.getImage()));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(visibleUser.getUsername());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profileViewerOptionBlock) {
                if (requestStatus.getText().toString().equals(getString(R.string.requestBlocked))) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                            .setTitle(String.format("%s %s", "Sorry but you cannot block", visibleUser.getUsername()))
                            .setMessage(String.format("%s %s %s", "Either you or", visibleUser.getUsername(), "issued a block, so you can't block a block"))
                            .setNegativeButton(R.string.generalOK, (dialog, i) -> dialog.dismiss())
                            .show();
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setBackground(AppCompatResources.getDrawable(this, R.color.basicBackground))
                            .setTitle(String.format("%s %s?", "Block", visibleUser.getUsername()))
                            .setMessage(String.format("%s %s", visibleUser.getUsername(), "will not be able to sent message or be your friend."))
                            .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                            .setPositiveButton("Block", (dialog, i) -> {
                                dialog.dismiss();
                                RequestEvents.block(myUser, visibleUser, ProfileViewer.this, new OnResponseListener() {
                                    @Override
                                    public void onSuccess(int code) {
                                        if (code == 0) {
                                            requestStatus.setText(getString(R.string.requestBlocked));
                                            requestStatus.setEnabled(false);
                                            onBackPressed();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).show();
                }
                return true;
            } else if (item.getItemId() == R.id.profileViewerOptionReport) {

                return true;
            }
            return false;
        });

        requestStatus.setOnClickListener(v -> {
            String state = requestStatus.getText().toString().trim();
            if (state.equals(getString(R.string.requestAccepted))) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                        .setTitle(String.format("%s %s %s?", "Remove", visibleUser.getUsername(), "from friends"))
                        .setMessage("You will not be able to message them but you will still be able to view their profile")
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton("Unfollow", (dialog, i) -> {
                            RequestEvents.delete(request, ProfileViewer.this, new OnResponseListener() {
                                @Override
                                public void onSuccess(int code) {
                                    if (code == 0) {
                                        requestStatus.setText(getString(R.string.requestNone));
                                        updateStats();
                                        toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), adapterFriends.getItemCount()));
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
                        .setTitle(String.format("%s %s?", "Do you want to remove the request to", visibleUser.getUsername()))
                        .setMessage("You will still be able to send again later if you changed your mind")
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton("Remove", (dialog, i) -> {
                            RequestEvents.delete(request, ProfileViewer.this, new OnResponseListener() {
                                @Override
                                public void onSuccess(int code) {
                                    if (code == 0) {
                                        requestStatus.setText(getString(R.string.requestNone));
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).show();
            } else if (state.equals(getString(R.string.requestNone))) {
                RequestEvents.sendRequest(myUser, visibleUser, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            requestStatus.setText(getString(R.string.requestWaiting));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (state.equals(getString(R.string.requestAnswer))) {
                RequestEvents.accept(request, ProfileViewer.this, new OnResponseListener() {
                    @Override
                    public void onSuccess(int code) {
                        if (code == 0) {
                            requestStatus.setText(getString(R.string.generalFriends));
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

        if (visibleUser.isProfileOpen()) {
            adapterFriends = new AdapterFriends(myUser, visibleUser, options, count ->
                    toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), count))
            );
            toolbar.setSubtitle(String.format(Locale.getDefault(), "%s: %d", getString(R.string.generalFriends), adapterFriends.getItemCount()));
            friendsView.setLayoutManager(new CustomLinearLayout(ProfileViewer.this, LinearLayoutManager.VERTICAL, false));
            friendsView.setAdapter(adapterFriends);
            friendsView.setItemAnimator(null);
        } else {
            toolbar.setSubtitle(getString(R.string.profileViewerPrivateProfile));
        }
    }

    private void updateStats() {
        if (!myUser.getId().equals(visibleUser.getId())) {
            RequestEvents.getRequestSnapshot(myUser, visibleUser, ProfileViewer.this, new OnRequestResponseListener() {
                @Override
                public void onSuccess(int code, Request requested) {
                    if (code == 0) {
                        request = requested;
                        switch (requested.getStatus()) {
                            case Friends:
                                requestStatus.setText(getString(R.string.requestAccepted));
                                break;
                            case Blocked:
                                requestStatus.setText(getString(R.string.requestBlocked));
                                break;
                            case Waiting:
                                if (requested.getSender().equals(myUser.getId())) {
                                    requestStatus.setText(getString(R.string.requestWaiting));
                                } else {
                                    requestStatus.setText(getString(R.string.requestAnswer));
                                }
                                break;
                            default:
                                requestStatus.setText(getString(R.string.requestNone));
                        }

                        if (requestStatus.getText().equals(getString(R.string.requestBlocked))) {
                            requestStatus.setEnabled(false);
                        }

                    } else {
                        requestStatus.setText(getString(R.string.requestNone));
                        request = null;
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(ProfileViewer.this, message, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AuthEventsDao.getUser() == null) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}

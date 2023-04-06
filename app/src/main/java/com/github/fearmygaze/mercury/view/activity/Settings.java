package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ChangeInformation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    //General
    ShapeableImageView goBack;

    //Account
    MaterialCardView changeEmail, changePassword, editProfile, signOut, closeAccount;

    //Preferences
    MaterialCardView pending, ignored, friends, content, theme;
    SwitchMaterial pendingSwitch, ignoreSwitch, friendsSwitch, contentSwitch;

    //Documents
    MaterialCardView privacy, terms;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        goBack = findViewById(R.id.settingsGoBack);
        changeEmail = findViewById(R.id.settingsAccountChangeEmail);
        changePassword = findViewById(R.id.settingsAccountChangePassword);
        editProfile = findViewById(R.id.settingsAccountEditProfile);
        signOut = findViewById(R.id.settingsAccountSignOut);
        closeAccount = findViewById(R.id.settingsAccountDelete);
        pending = findViewById(R.id.settingsPreferencesPending);
        pendingSwitch = findViewById(R.id.settingsPreferencesPendingSwitch);
        ignored = findViewById(R.id.settingsPreferencesIgnored);
        ignoreSwitch = findViewById(R.id.settingsPreferencesIgnoredSwitch);
        friends = findViewById(R.id.settingsPreferencesFriends);
        friendsSwitch = findViewById(R.id.settingsPreferencesFriendsSwitch);
        content = findViewById(R.id.settingsPreferencesContent);
        contentSwitch = findViewById(R.id.settingsPreferencesContentSwitch);
        theme = findViewById(R.id.settingsPreferencesAlternate);
        privacy = findViewById(R.id.settingsDocumentsPrivacy);
        terms = findViewById(R.id.settingsDocumentsTerms);

        user = AppDatabase.getInstance(Settings.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());

        pendingSwitch.setChecked(Tools.getPreference("showPending", Settings.this));
        ignoreSwitch.setChecked(Tools.getPreference("showIgnored", Settings.this));
        contentSwitch.setChecked(Tools.getPreference("showImages", Settings.this));

        Auth.getShowFriends(user.userUID, new Auth.OnResponseListener() {
            @Override
            public void onResult(int resultCode) {
                if (resultCode == 1) {
                    friendsSwitch.setChecked(true);
                } else if (resultCode == 0) {
                    friendsSwitch.setChecked(false);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        goBack.setOnClickListener(v -> onBackPressed());

        changeEmail.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra("userID", user.userUID)
                    .putExtra("type", "email"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra("userID", user.userUID)
                    .putExtra("type", "password"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        editProfile.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ProfileEdit.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        signOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            onBackPressed();
        });

        closeAccount.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(Settings.this)
                    .setBackground(AppCompatResources.getDrawable(Settings.this, R.color.basicBackground))
                    .setCancelable(true)
                    .setTitle("Danger")
                    .setMessage("This Action Cannot be undone")
                    .setPositiveButton("Proceed", (dialog, which) ->{}
//                            Auth.deleteAccount("", new Auth.OnResponseListener() {
//                                @Override
//                                public void onResult(int resultCode) {
//                                    if (resultCode == 1) {
////                                        AppDatabase.getInstance(Settings.this).userDao().deleteUser(user);
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(String message) {
//                                    Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
//                                }
//                            })
                    )
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .show();
        });

        pending.setOnClickListener(v -> {
            pendingSwitch.setChecked(!pendingSwitch.isChecked());
            Tools.writePreference("showPending", pendingSwitch.isChecked(), Settings.this);
        });

        ignored.setOnClickListener(v -> {
            ignoreSwitch.setChecked(!ignoreSwitch.isChecked());
            Tools.writePreference("showIgnored", ignoreSwitch.isChecked(), Settings.this);
        });

        friends.setOnClickListener(v -> {
            friendsSwitch.setChecked(!friendsSwitch.isChecked());
            Auth.setShowFriends(user, friendsSwitch.isChecked(), new Auth.OnResponseListener() {
                @Override
                public void onResult(int resultCode) {
                    if (resultCode == 1) {
                        AppDatabase.getInstance(Settings.this).userDao().updateUser(new User(
                                user.userUID,
                                user.email,
                                user.username,
                                user.name,
                                user.imageURL,
                                user.notificationToken,
                                user.status,
                                user.location,
                                user.job,
                                user.website,
                                friendsSwitch.isChecked(),
                                user.createdAt
                        ));
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        content.setOnClickListener(v -> {
            contentSwitch.setChecked(!contentSwitch.isChecked());
            Tools.writePreference("showImages", contentSwitch.isChecked(), Settings.this);
        });

        theme.setOnClickListener(v -> {

        });

        privacy.setOnClickListener(v -> {

        });

        terms.setOnClickListener(v -> {

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
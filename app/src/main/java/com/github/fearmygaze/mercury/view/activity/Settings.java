package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ChangeInformation;
import com.github.fearmygaze.mercury.view.util.ProfileEdit;
import com.github.fearmygaze.mercury.view.util.ShowBlockedUsers;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    //General
    MaterialToolbar toolbar;

    //Account
    MaterialCardView changeEmail, changePassword, editProfile, signOut;

    //Privacy & Safety
    MaterialCardView showBlocked, profile, content;
    SwitchMaterial profileSwitch, contentSwitch;

    //Preferences
    MaterialCardView theme;

    //Documents
    MaterialCardView privacy, terms;

    //Danger
    MaterialCardView closeAccount;

    Bundle bundle;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settingsToolBar);
        changeEmail = findViewById(R.id.settingsAccountChangeEmail);
        changePassword = findViewById(R.id.settingsAccountChangePassword);
        editProfile = findViewById(R.id.settingsAccountEditProfile);
        signOut = findViewById(R.id.settingsAccountSignOut);

        profile = findViewById(R.id.settingsPrivacyPublicProfile);
        profileSwitch = findViewById(R.id.settingsPrivacyPublicProfileSwitch);
        content = findViewById(R.id.settingsPrivacyViewContent);
        contentSwitch = findViewById(R.id.settingsPreferencesContentSwitch);
        showBlocked = findViewById(R.id.settingsPrivacyBlocked);

        theme = findViewById(R.id.settingsPreferencesAlternate);

        privacy = findViewById(R.id.settingsDocumentsPrivacy);
        terms = findViewById(R.id.settingsDocumentsTerms);

        closeAccount = findViewById(R.id.settingsDangerCloseAccount);

        bundle = getIntent().getExtras();

        if (bundle == null) onBackPressed();

        user = bundle.getParcelable(User.PARCEL);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        contentSwitch.setChecked(Tools.getBoolPreference("showImages", Settings.this));
        profileSwitch.setChecked(user.isProfileOpen());

        changeEmail.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User.ID, user.getId())
                    .putExtra("type", "email"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User.ID, user.getId())
                    .putExtra("type", "password"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        editProfile.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ProfileEdit.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        signOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            User.deleteRoomUser(user, Settings.this);
            onBackPressed();
        });

        showBlocked.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ShowBlockedUsers.class)
                    .putExtra(User.PARCEL, user));
        });

        closeAccount.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(Settings.this)
                    .setBackground(AppCompatResources.getDrawable(Settings.this, R.color.basicBackground))
                    .setCancelable(true)
                    .setTitle(getString(R.string.settingsDialogTitle))
                    .setMessage(String.format("%s, %s %s", getString(R.string.settingsDialogMessagePart1), user.getUsername(), getString(R.string.settingsDialogMessagePart2)))
                    .setNegativeButton(getString(R.string.generalCancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getString(R.string.generalDelete), (dialog, which) -> {
                        Toast.makeText(Settings.this, "Not implemented", Toast.LENGTH_SHORT).show();
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
                    })
                    .show();
        });

        profile.setOnClickListener(v -> {
            profileSwitch.setChecked(!profileSwitch.isChecked());
            Auth.updateState(user.getId(), profileSwitch.isChecked(), Settings.this, new OnResponseListener() {
                @Override
                public void onSuccess(int code) {
                    if (code == 0) {
                        Toast.makeText(Settings.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.UIAction;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.AccountActions.ChangeInformation;
import com.github.fearmygaze.mercury.view.util.AccountActions.ProfileEdit;
import com.github.fearmygaze.mercury.view.util.ShowBlockedUsers;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {

    //General
    MaterialToolbar toolbar;

    //Account
    MaterialCardView changeEmail, changePassword, editProfile, signOut;

    //Privacy & Safety
    MaterialCardView showBlocked, profile, content;
    SwitchMaterial profileSwitch, contentSwitch;

    //Preferences
    MaterialCardView accent;

    //Documents
    MaterialCardView faq, privacy, terms;

    //Danger
    MaterialCardView closeAccount;

    User1 user;
    Bundle bundle;
    Auth auth;

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

        accent = findViewById(R.id.settingsPreferencesAccent);
        faq = findViewById(R.id.settingsDocumentsFAQ);
        privacy = findViewById(R.id.settingsDocumentsPrivacy);
        terms = findViewById(R.id.settingsDocumentsTerms);

        closeAccount = findViewById(R.id.settingsDangerCloseAccount);

        bundle = getIntent().getExtras();

        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User1.PARCEL);
        if (user == null) onBackPressed();

        auth = new Auth(Settings.this);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        contentSwitch.setChecked(Tools.getBoolPreference("showImages", Settings.this));
        profileSwitch.setChecked(user.isProfileOpen());

        changeEmail.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User.PARCEL, user)
                    .putExtra("type", "email"));
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User.PARCEL, user)
                    .putExtra("type", "password"));
        });

        editProfile.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ProfileEdit.class)
                    .putExtra(User.PARCEL, user));
        });

        signOut.setOnClickListener(v -> {
            new PrivatePreference(this).clearAllValues();
            auth.signOut(true);
            UIAction.flushActivityStuck(v.getContext(), Welcome.class);
        });

        showBlocked.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ShowBlockedUsers.class)
                    .putExtra(User1.PARCEL, user));
        });

        closeAccount.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User1.PARCEL, user)
                    .putExtra("type", "delete"));
        });

        profile.setOnClickListener(v -> {
            auth.updateProfileVisibility(user.getId(), !profile.isChecked(), new CallBackResponse<String>() {
                @Override
                public void onSuccess(String message) {
                    profileSwitch.setChecked(!profileSwitch.isChecked());
                    this.onFailure(message);
                }

                @Override
                public void onError(String message) {
                    this.onFailure(message);
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

        accent.setOnClickListener(v -> {

        });

        faq.setOnClickListener(v -> {

        });

        privacy.setOnClickListener(v -> {

        });

        terms.setOnClickListener(v -> {

        });

    }
}

package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.UserActions;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.AccountActions.ChangeInformation;
import com.github.fearmygaze.mercury.view.util.ProfileEdit;
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

    User user;
    Bundle bundle;
    UserActions userActions;

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
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) onBackPressed();

        userActions = new UserActions(Settings.this);

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
            userActions.signOut();
            new PrivatePreference(this).clearAllValues();
            User.deleteRoomUser(user, Settings.this);
            Intent intent = new Intent(Settings.this, Welcome.class); //This resets the activity stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        showBlocked.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ShowBlockedUsers.class)
                    .putExtra(User.PARCEL, user));
        });

        closeAccount.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra(User.PARCEL, user)
                    .putExtra("type", "delete"));
        });

        profile.setOnClickListener(v -> {//TODO: maybe move the setChecked inside the onSuccess
            profileSwitch.setChecked(!profileSwitch.isChecked());
            userActions.updateProfileVisibility(user.getId(), profile.isChecked(), new CallBackResponse<String>() {
                @Override
                public void onSuccess(String object) {
                    this.onFailure(object);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

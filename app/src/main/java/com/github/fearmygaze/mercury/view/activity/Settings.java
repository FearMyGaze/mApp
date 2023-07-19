package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ChangeInformation;
import com.github.fearmygaze.mercury.view.util.ProfileEdit;
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
    MaterialCardView profile, content, theme;
    SwitchMaterial profileSwitch, contentSwitch;

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
        profile = findViewById(R.id.settingsPreferencesPublicProfile);
        profileSwitch = findViewById(R.id.settingsPreferencesPublicProfileSwitch);
        content = findViewById(R.id.settingsPreferencesContent);
        contentSwitch = findViewById(R.id.settingsPreferencesContentSwitch);
        theme = findViewById(R.id.settingsPreferencesAlternate);
        privacy = findViewById(R.id.settingsDocumentsPrivacy);
        terms = findViewById(R.id.settingsDocumentsTerms);

        user = AppDatabase.getInstance(Settings.this).userDao().getUserByUserID(FirebaseAuth.getInstance().getUid());

        contentSwitch.setChecked(Tools.getPreference("showImages", Settings.this));
        profileSwitch.setChecked(user.getIsProfileOpen());

        goBack.setOnClickListener(v -> onBackPressed());

        changeEmail.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra("userID", user.getId())
                    .putExtra("type", "email"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, ChangeInformation.class)
                    .putExtra("userID", user.getId())
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

        closeAccount.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(Settings.this)
                    .setBackground(AppCompatResources.getDrawable(Settings.this, R.color.basicBackground))
                    .setCancelable(true)
                    .setTitle("Danger")
                    .setMessage("This Action Cannot be undone")
                    .setPositiveButton("Proceed", (dialog, which) -> {
                            }
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
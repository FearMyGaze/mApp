package com.github.fearmygaze.mercury.view.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ProfileEdit extends AppCompatActivity {

    MaterialButton choose, save;
    ShapeableImageView back, userImage;

    TextInputLayout statusLayout, locationLayout, jobLayout, websiteLayout;
    TextInputEditText statusCell, locationCell, jobCell, websiteCell;

    Bundle bundle;
    User user;
    Uri imageData;
    boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        back = findViewById(R.id.profileEditGoBack);
        save = findViewById(R.id.profileEditSave);
        choose = findViewById(R.id.profileEditChoose);
        userImage = findViewById(R.id.profileEditUserImage);
        statusLayout = findViewById(R.id.profileEditStatusError);
        statusCell = findViewById(R.id.profileEditStatus);
        locationLayout = findViewById(R.id.profileEditLocationError);
        locationCell = findViewById(R.id.profileEditLocation);
        jobLayout = findViewById(R.id.profileEditJobError);
        jobCell = findViewById(R.id.profileEditJob);
        websiteLayout = findViewById(R.id.profileEditWebsiteError);
        websiteCell = findViewById(R.id.profileEditWebsite);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) onBackPressed();

        Tools.profileImage(user.getImage(), ProfileEdit.this).into(userImage);
        statusCell.setText(user.getStatus());
        locationCell.setText(user.getLocation());
        jobCell.setText(user.getJob());
        websiteCell.setText(user.getWebsite());

        websiteCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                save.setEnabled(RegEx.isUrlValid(websiteCell, websiteLayout, ProfileEdit.this));
            }
        });

        back.setOnClickListener(v -> onBackPressed());
        save.setOnClickListener(v -> {
            if (Objects.requireNonNull(statusCell.getText()).toString().trim().equals(user.getStatus()) &&
                    Objects.requireNonNull(locationCell.getText()).toString().trim().equals(user.getLocation()) &&
                    Objects.requireNonNull(jobCell.getText()).toString().trim().equals(user.getJob()) &&
                    Objects.requireNonNull(websiteCell.getText()).toString().trim().equals(user.getWebsite()) &&
                    imageData == null) {
                new MaterialAlertDialogBuilder(ProfileEdit.this)
                        .setBackground(AppCompatResources.getDrawable(ProfileEdit.this, R.color.basicBackground))
                        .setTitle(getString(R.string.profileEditDialogTitle1))
                        .setMessage(getString(R.string.profileEditDialogMessage1))
                        .setPositiveButton(R.string.generalOK, (dialog, i) -> dialog.dismiss())
                        .show();
            } else {
                new MaterialAlertDialogBuilder(ProfileEdit.this)
                        .setCancelable(false)
                        .setBackground(AppCompatResources.getDrawable(ProfileEdit.this, R.color.basicBackground))
                        .setTitle(getString(R.string.profileEditDialogTitle2))
                        .setMessage(getString(R.string.profileEditDialogMessage2))
                        .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                        .setPositiveButton(R.string.generalOK, (dialog, i) -> {
                            user.setStatus(Objects.requireNonNull(statusCell.getText()).toString().trim());
                            user.setLocation(Objects.requireNonNull(locationCell.getText()).toString().trim());
                            user.setLocationL(user.getLocation().toLowerCase());
                            user.setJob(Objects.requireNonNull(jobCell.getText()).toString().trim());
                            user.setJobL(user.getJob().toLowerCase());
                            user.setWebsite(Objects.requireNonNull(websiteCell.getText()).toString().trim());
                            Auth.updateProfile(user, imageChanged, imageData, ProfileEdit.this, new OnResponseListener() {
                                @Override
                                public void onSuccess(int code) {
                                    if (code == 0) {
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    dialog.dismiss();
                                    Toast.makeText(ProfileEdit.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .show();
            }
        });
        choose.setOnClickListener(v -> pickImage.launch(Tools.imageSelector()));
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case RESULT_OK:
                        if (result.getData() != null) {
                            imageData = result.getData().getData();
                            Glide.with(ProfileEdit.this).load(imageData).centerCrop().into(userImage);
                            imageChanged = true;
                        }
                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(ProfileEdit.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}

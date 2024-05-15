package com.github.fearmygaze.mercury.view.util.AccountActions;

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
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
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

    MaterialButton chooseBtn, saveBtn;
    ShapeableImageView goBackBtn, userImage;

    TextInputLayout fullNameLayout, bioLayout, locationLayout, jobLayout, websiteLayout;
    TextInputEditText fullNameCell, bioCell, locationCell, jobCell, websiteCell;

    Bundle bundle;
    User1 user;
    Uri imageData;
    boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        goBackBtn = findViewById(R.id.profileEditGoBack);
        saveBtn = findViewById(R.id.profileEditSave);
        chooseBtn = findViewById(R.id.profileEditChoose);
        userImage = findViewById(R.id.profileEditUserImage);
        fullNameLayout = findViewById(R.id.profileEditFullNameError);
        fullNameCell = findViewById(R.id.profileEditFullName);
        bioLayout = findViewById(R.id.profileEditStatusError);
        bioCell = findViewById(R.id.profileEditStatus);
        locationLayout = findViewById(R.id.profileEditLocationError);
        locationCell = findViewById(R.id.profileEditLocation);
        jobLayout = findViewById(R.id.profileEditJobError);
        jobCell = findViewById(R.id.profileEditJob);
        websiteLayout = findViewById(R.id.profileEditWebsiteError);
        websiteCell = findViewById(R.id.profileEditWebsite);

        bundle = getIntent().getExtras();
        if (bundle == null) finish();
        user = bundle.getParcelable(User.PARCEL);
        if (user == null) finish();

        goBackBtn.setOnClickListener(v -> onBackPressed());

        fullNameCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty())
                    RegEx.isNameValid(fullNameCell, fullNameLayout, ProfileEdit.this);
                else
                    Tools.setErrorToLayout(fullNameLayout, "", false);
            }
        });

        websiteCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RegEx.isUrlValid(websiteCell, websiteLayout, ProfileEdit.this);
            }
        });

        chooseBtn.setOnClickListener(v -> pickImage.launch(new Intent(Intent.ACTION_PICK)
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false))
        );

        saveBtn.setOnClickListener(v -> {
            String fullName = Objects.requireNonNull(fullNameCell.getText()).toString().trim();
            String bio = Objects.requireNonNull(bioCell.getText()).toString().trim();
            String location = Objects.requireNonNull(locationCell.getText()).toString().trim();
            String job = Objects.requireNonNull(jobCell.getText()).toString().trim();
            String website = Objects.requireNonNull(websiteCell.getText()).toString().trim();

            if (fullNameLayout.isErrorEnabled() || bioLayout.isErrorEnabled() ||
                    locationLayout.isErrorEnabled() || jobLayout.isErrorEnabled() ||
                    websiteLayout.isErrorEnabled()) {
                new MaterialAlertDialogBuilder(ProfileEdit.this)
                        .setBackground(AppCompatResources.getDrawable(ProfileEdit.this, R.color.basicBackground))
                        .setTitle(getString(R.string.profileEditDialogTitle1))
                        .setMessage("You need to fix the errors before updating your profile")
                        .setPositiveButton(R.string.generalOK, (dialog, i) -> dialog.dismiss())
                        .show();
            } else if (fullName.equals(user.getUsername()) && bio.equals(user.getBio()) &&
                    location.equals(user.getLocation()) && job.equals(user.getJob()) &&
                    website.equals(user.getWebsite()) && imageData == null) {
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
                            user.setBio(bio);
                            user.setLocation(location);
                            user.setLocationL(location.toLowerCase());
                            user.setJob(job);
                            user.setJobL(job.toLowerCase());
                            user.setWebsite(website);
                            new Auth(ProfileEdit.this).updateProfile(imageData, user, new CallBackResponse<String>() {
                                @Override
                                public void onSuccess(String object) {
                                    dialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onError(String message) {
                                    this.onFailure(message);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tools.profileImage(user.getImage(), ProfileEdit.this).into(userImage);
        fullNameCell.setText(user.getUsername());
        bioCell.setText(user.getBio());
        locationCell.setText(user.getLocation());
        jobCell.setText(user.getJob());
        websiteCell.setText(user.getWebsite());
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
}

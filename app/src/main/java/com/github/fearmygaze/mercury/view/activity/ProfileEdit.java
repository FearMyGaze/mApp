package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class ProfileEdit extends AppCompatActivity {

    MaterialButton choose, save;
    ShapeableImageView back, userImage;

    TextInputLayout nameLayout, statusLayout, locationLayout, jobLayout, websiteLayout;
    TextInputEditText nameCell, statusCell, locationCell, jobCell, websiteCell;

    User user;
    Uri imageData;
    boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        back = findViewById(R.id.profileEditGoBack);
        save = findViewById(R.id.profileEditSave);
        choose = findViewById(R.id.profileEditChoose);
        userImage = findViewById(R.id.profileEditUserImage);
        nameLayout = findViewById(R.id.profileEditNameError);
        nameCell = findViewById(R.id.profileEditName);
        statusLayout = findViewById(R.id.profileEditStatusError);
        statusCell = findViewById(R.id.profileEditStatus);
        locationLayout = findViewById(R.id.profileEditLocationError);
        locationCell = findViewById(R.id.profileEditLocation);
        jobLayout = findViewById(R.id.profileEditJobError);
        jobCell = findViewById(R.id.profileEditJob);
        websiteLayout = findViewById(R.id.profileEditWebsiteError);
        websiteCell = findViewById(R.id.profileEditWebsite);

        user = AppDatabase.getInstance(ProfileEdit.this).userDao().getUserByUserUID(FirebaseAuth.getInstance().getUid());

        Glide.with(ProfileEdit.this).load(Uri.parse(user.imageURL)).centerInside().into(userImage);
        nameCell.setText(user.name);
        statusCell.setText(user.status);
        locationCell.setText(user.location);
        jobCell.setText(user.job);
        websiteCell.setText(user.website);

        nameCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RegEx.isUsernameValid(nameCell, nameLayout, ProfileEdit.this);
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


        back.setOnClickListener(v -> onBackPressed());
        save.setOnClickListener(v -> sendData());
        choose.setOnClickListener(v -> pickImage.launch(Tools.imageSelector()));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void sendData() {
        if (!nameLayout.isErrorEnabled() && !websiteLayout.isErrorEnabled()) {
            if (!changed) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(user.userUID)
                        .setValue(new User(
                                user.userUID,
                                user.email,
                                user.username,
                                Objects.requireNonNull(nameCell.getText()).toString().trim(),
                                user.imageURL,
                                user.notificationToken,
                                Objects.requireNonNull(statusCell.getText()).toString().trim(),
                                Objects.requireNonNull(locationCell.getText()).toString().trim(),
                                Objects.requireNonNull(jobCell.getText()).toString().trim(),
                                Objects.requireNonNull(websiteCell.getText()).toString().trim(),
                                user.showFriends,
                                user.createdAt).toMap(false))
                        .addOnSuccessListener(unused -> {
                            AppDatabase.getInstance(ProfileEdit.this).userDao().updateUser(new User(
                                    user.userUID,
                                    user.email,
                                    user.username,
                                    Objects.requireNonNull(nameCell.getText()).toString().trim(),
                                    user.imageURL,
                                    user.notificationToken,
                                    Objects.requireNonNull(statusCell.getText()).toString().trim(),
                                    Objects.requireNonNull(locationCell.getText()).toString().trim(),
                                    Objects.requireNonNull(jobCell.getText()).toString().trim(),
                                    Objects.requireNonNull(websiteCell.getText()).toString().trim(),
                                    user.showFriends,
                                    user.createdAt
                            ));
                            onBackPressed();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profileImages/" + UUID.randomUUID().toString().trim());
                storageRef.putFile(imageData).addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(user.userUID)
                                        .setValue(new User(
                                                user.userUID,
                                                user.email,
                                                user.username,
                                                Objects.requireNonNull(nameCell.getText()).toString().trim(),
                                                String.valueOf(uri),
                                                user.notificationToken,
                                                Objects.requireNonNull(statusCell.getText()).toString().trim(),
                                                Objects.requireNonNull(locationCell.getText()).toString().trim(),
                                                Objects.requireNonNull(jobCell.getText()).toString().trim(),
                                                Objects.requireNonNull(websiteCell.getText()).toString().trim(),
                                                user.showFriends,
                                                user.createdAt).toMap(false))
                                        .addOnSuccessListener(unused -> {
                                            AppDatabase.getInstance(ProfileEdit.this).userDao().updateUser(new User(
                                                    user.userUID,
                                                    user.email,
                                                    user.username,
                                                    Objects.requireNonNull(nameCell.getText()).toString().trim(),
                                                    String.valueOf(uri),
                                                    user.notificationToken,
                                                    Objects.requireNonNull(statusCell.getText()).toString().trim(),
                                                    Objects.requireNonNull(locationCell.getText()).toString().trim(),
                                                    Objects.requireNonNull(jobCell.getText()).toString().trim(),
                                                    Objects.requireNonNull(websiteCell.getText()).toString().trim(),
                                                    user.showFriends,
                                                    user.createdAt
                                            ));
                                            onBackPressed();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                        ).addOnFailureListener(e -> Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                ).addOnFailureListener(e -> Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case RESULT_OK:
                        if (result.getData() != null) {
                            imageData = result.getData().getData();
                            Glide.with(ProfileEdit.this).load(imageData).centerInside().into(userImage);
                            changed = true;
                        }
                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(ProfileEdit.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            });
}
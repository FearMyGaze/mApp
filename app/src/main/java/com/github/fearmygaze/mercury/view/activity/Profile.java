package com.github.fearmygaze.mercury.view.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.UserController;
import com.github.fearmygaze.mercury.custom.EventNotifier;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.interfaces.IFormUpdate;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.util.RegEx;
import com.github.fearmygaze.mercury.util.TextHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Profile extends AppCompatActivity {

    ShapeableImageView userImage, dialogImage;
    MaterialTextView username, userEmail, faq;
    MaterialButton changePassword, changeProfilePicture, update;

    PrivatePreference preference;
    AppDatabase database;
    User user;

    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        database = AppDatabase.getInstance(Profile.this);
        preference = new PrivatePreference(Profile.this);
        user = database.userDao().getUserByID(preference.getInt("id"));

        userImage = findViewById(R.id.profileUserImage);
        username = findViewById(R.id.profileUsername);
        userEmail = findViewById(R.id.profileUserEmail);
        changePassword = findViewById(R.id.profileChangePassword);
        changeProfilePicture = findViewById(R.id.profileChangeImage);
        faq = findViewById(R.id.profileFAQ);

        username.setText(user.getUsername());
        userEmail.setText(user.getEmail());

        updateImage();

        changePassword.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_update_password);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextInputLayout oldPasswordError = dialog.findViewById(R.id.dialogUpdatePasswordOldPasswordError);
            TextInputEditText oldPassword = dialog.findViewById(R.id.dialogUpdatePasswordOldPassword);

            TextInputLayout newPasswordError = dialog.findViewById(R.id.dialogUpdatePasswordNewPasswordError);
            TextInputEditText newPassword = dialog.findViewById(R.id.dialogUpdatePasswordNewPassword);

            MaterialButton cancel = dialog.findViewById(R.id.dialogUpdatePasswordCancel);
            MaterialButton confirm = dialog.findViewById(R.id.dialogUpdatePasswordConfirm);

            oldPassword.addTextChangedListener(new TextHandler(oldPasswordError));
            newPassword.addTextChangedListener(new TextHandler(newPasswordError));

            cancel.setOnClickListener(v1 -> dialog.cancel());

            confirm.setOnClickListener(v1 -> {
                String _oldPassword = Objects.requireNonNull(oldPassword.getText()).toString();
                String _newPassword = Objects.requireNonNull(newPassword.getText()).toString();

                if (RegEx.isPasswordValidAndDifferent(oldPassword, oldPasswordError, newPassword, newPasswordError, 300, getApplicationContext())) {
                    UserController.updatePassword(user.getId(), _newPassword, _oldPassword, getApplicationContext(), new IFormUpdate() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onValidationError(String message) {

                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            dialog.show();
        });

        changeProfilePicture.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_update_image);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialogImage = dialog.findViewById(R.id.dialogUpdateImageUserImage);

            MaterialButton cancel = dialog.findViewById(R.id.dialogUpdateImageCancel);
            MaterialButton choose = dialog.findViewById(R.id.dialogUpdateImageChoose);
            update = dialog.findViewById(R.id.dialogUpdateImageConfirm);

            Glide.with(this)
                    .load(user.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .apply(RequestOptions.overrideOf(1024, 1024))
                    .into(dialogImage);

            cancel.setOnClickListener(v1 -> dialog.cancel());

            choose.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                pickImage.launch(intent);
            });

            update.setOnClickListener(v1 -> UserController.updateImage(user, base64Image, v1.getContext(), new IFormUpdate() {
                @Override
                public void onSuccess(String message) {
                    user = database.userDao().getUserByID(preference.getInt("id"));
                    Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    updateImage();
                }

                @Override
                public void onValidationError(String message) {

                }

                @Override
                public void onError(String message) {
                    Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                }
            }));
            dialog.show();
        });

        faq.setOnClickListener(v -> {
            Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
        });

    }

    private void updateImage() {
        Glide.with(this)
                .load(user.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.overrideOf(1024, 1024))
                .into(userImage);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    try {
                        Bitmap output = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 1024, 1024, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        output.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        base64Image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                        dialogImage.setImageURI(uri);
                        update.setEnabled(true);
                    } catch (IOException e) {
                        EventNotifier.event(faq, "Error: " + e.getMessage(), EventNotifier.LENGTH_LONG);
                    }
                }
            }
    );

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
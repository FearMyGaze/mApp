package com.fearmygaze.mApp.view.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.Controller.UserController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.util.RegEx;
import com.fearmygaze.mApp.util.TextHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class Profile extends AppCompatActivity {

    ShapeableImageView userImage;
    MaterialTextView username, userEmail, faq;
    MaterialButton changePassword, add2FA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {// TODO: Add  the profile faq add 2FA and the QR image
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.profileUserImage);
        username = findViewById(R.id.profileUsername);
        userEmail = findViewById(R.id.profileUserEmail);
        changePassword = findViewById(R.id.profileChangePassword);
        add2FA = findViewById(R.id.profileAdd2FA);
        faq = findViewById(R.id.profileFAQ);

        Glide.with(this)
                .load("https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png")
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(userImage);

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
                    UserController.updatePassword(20, _newPassword, _oldPassword, getApplicationContext(), new IVolley() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            dialog.show();

        });

        add2FA.setOnClickListener(v -> {

        });

        faq.setOnClickListener(v -> {
            Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
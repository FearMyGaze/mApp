package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.EventNotifier;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        userImage = findViewById(R.id.profileUserImage);
        username = findViewById(R.id.profileUsername);
        userEmail = findViewById(R.id.profileUserEmail);
        changePassword = findViewById(R.id.profileChangePassword);
        changeProfilePicture = findViewById(R.id.profileChangeImage);
        faq = findViewById(R.id.profileFAQ);


        faq.setOnClickListener(v -> {
            Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
        });

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
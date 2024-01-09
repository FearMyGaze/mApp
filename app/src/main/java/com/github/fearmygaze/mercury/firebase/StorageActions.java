package com.github.fearmygaze.mercury.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.github.fearmygaze.mercury.firebase.dao.StorageActionsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class StorageActions implements StorageActionsDao {

    private final Context ctx;
    private final FirebaseStorage storage;

    public StorageActions(Context context) {
        this.ctx = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public void uploadFile(Uri file, String fileName, String location, CallBackResponse<String> callBackResponse) {
        StorageReference reference = storage.getReference().child(location);
        StorageReference imageRef = reference.child(fileName);
        imageRef.putFile(file)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to upload the file"))
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl()
                            .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the download link"))
                            .addOnSuccessListener(uri -> callBackResponse.onSuccess(String.valueOf(uri)));
                });
    }

    @Override
    public String generateProfileImageName(Uri uri) {
        ContentResolver contentResolver = ctx.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.ENGLISH);
        String fileName = dateFormat.format(Calendar.getInstance().getTime());

        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            return String.format(Locale.ENGLISH, "%d_%s.%s", new Random().nextInt(), fileName, mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)));
        } else {
            return String.format(Locale.ENGLISH, "%d_%s.%s", new Random().nextInt(), fileName, MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        }
    }


}

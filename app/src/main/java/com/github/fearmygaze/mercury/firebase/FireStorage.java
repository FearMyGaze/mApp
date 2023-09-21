package com.github.fearmygaze.mercury.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class FireStorage {

    public static void updateProfileImage(User user, Uri uri, Context context, OnResponseListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(User.IMAGE_COLLECTION);
        StorageReference imageRef = storageRef.child(generateFileName(uri, context));
        imageRef.putFile(uri)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                        .addOnSuccessListener(url -> Auth.updateInformation(User.updateRoomImage(user.getId(), url, context), context, listener)));
    }

    public static String generateFileName(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.ENGLISH);
        String fileName = dateFormat.format(Calendar.getInstance().getTime());

        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            return String.format(Locale.ENGLISH, "%s_%d.%s", fileName, new Random().nextInt(), mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)));
        } else {
            return String.format(Locale.ENGLISH, "%s_%d.%s", fileName, new Random().nextInt(), MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        }
    }

}

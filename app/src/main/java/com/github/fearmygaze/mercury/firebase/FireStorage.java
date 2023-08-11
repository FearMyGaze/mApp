package com.github.fearmygaze.mercury.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class FireStorage {

    public static void uploadDefaultImage(String username, Context context, OnDataResponseListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(User.IMAGE_COLLECTION);
        StorageReference defFile = storageRef.child(context.getString(R.string.default_image));
        String fileType = defFile.getName().substring(defFile.getName().lastIndexOf('.') + 1);
        StorageReference updFile = storageRef.child(username + "." + fileType);
        File localFile;
        try {
            localFile = File.createTempFile(username, fileType);
            defFile.getFile(localFile)
                    .addOnFailureListener(e -> {
                        localFile.delete();
                        listener.onFailure(e.getMessage());
                    })
                    .addOnSuccessListener(taskSnapshot -> updFile
                            .putFile(Uri.fromFile(localFile))
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                            .addOnSuccessListener(taskSnapshot1 -> updFile.getDownloadUrl()
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(url -> {
                                        localFile.delete();
                                        listener.onSuccess(0, url);
                                    })));
        } catch (IOException e) {
            listener.onFailure(e.getMessage());
        }
    }

    public static void updateProfileImage(User user, Uri uri, Context context, OnResponseListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(User.IMAGE_COLLECTION);
        String currFileName = getFileNameFromLink(user.getImage());
        String newFilename = generateFileName(uri, user.getUsername(), context);

        if (getFileExtension(currFileName).equals(getFileExtension(newFilename))) {
            StorageReference localRef = storageRef.child(generateFileName(uri, user.getUsername(), context));
            localRef.putFile(uri)
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    .addOnSuccessListener(taskSnapshot -> localRef.getDownloadUrl()
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                            .addOnSuccessListener(url -> Auth.updateInformation(User.updateRoomImage(user.getId(), url, context), context, listener)));
        } else {
            StorageReference localRef = storageRef.child(currFileName);
            localRef.delete()
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    .addOnSuccessListener(unused -> {
                        StorageReference newRef = storageRef.child(generateFileName(uri, user.getUsername(), context));
                        newRef.putFile(uri)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(taskSnapshot -> newRef.getDownloadUrl()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(url -> Auth.updateInformation(User.updateRoomImage(user.getId(), url, context), context, listener)));
                    });
        }
    }


    public static String generateFileName(Uri image, String username, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (ContentResolver.SCHEME_CONTENT.equals(image.getScheme())) {
            return username + "." + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(image));
        } else {
            return username + "." + MimeTypeMap.getFileExtensionFromUrl(image.toString());
        }
    }

    public static String generateFileName(Uri image, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH);

        String fileName = dateFormat.format(calendar.getTime());

        if (ContentResolver.SCHEME_CONTENT.equals(image.getScheme())) {
            return fileName + "_" + new Random().nextInt() + "." + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(image));
        } else {
            return fileName + "_" + new Random().nextInt() + "." + MimeTypeMap.getFileExtensionFromUrl(image.toString());
        }
    }

    public static String getFileExtensionFromLink(String url, String username) {
        int usernameIndex = url.indexOf(username);
        if (usernameIndex != -1) {
            int questionMarkIndex = url.indexOf("?", usernameIndex);
            if (questionMarkIndex != -1) {
                return url.substring(usernameIndex + username.length(), questionMarkIndex);
            }
        }
        return "";
    }

    private static String getFileNameFromLink(String url) {
        int usernameIndex = url.indexOf("%2F");
        if (usernameIndex != -1) {
            int questionMarkIndex = url.indexOf("?", usernameIndex);
            if (questionMarkIndex != -1) {
                return url.substring(usernameIndex + "%2F".length(), questionMarkIndex);
            }
        }
        return "";
    }

    public static String getFileExtension(String filename) {
        int index = filename.indexOf(".");
        if (index > 0 && index < filename.length() - 1) {
            return filename.substring(index + 1);
        }
        return "";
    }
}

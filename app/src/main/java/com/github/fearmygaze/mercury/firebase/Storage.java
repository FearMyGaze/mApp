package com.github.fearmygaze.mercury.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class Storage implements StorageDao{

    private final Context ctx;
    private final FirebaseStorage storage;

    public Storage(Context context) {
        this.ctx = context;
        this.storage = FirebaseStorage.getInstance();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadProfileImage(Uri image, CallBackResponse<String> response) {
        uploadImage(image, "/profileImages", new CallBackResponse<String>() {
            @Override
            public void onSuccess(String imageUrl) {
                response.onSuccess(imageUrl);
            }

            @Override
            public void onError(String message) {
                response.onError(message);
            }

            @Override
            public void onFailure(String message) {
                response.onFailure(message);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param image    The selected image
     * @param location The location we want the file to upload
     * @param callback Returns the sate of the upload
     */
    private void uploadImage(Uri image, String location, CallBackResponse<String> callback) {
        StorageReference locationReference = storage.getReference().child(location);
        StorageReference imageReference = locationReference.child(generateFileName(image));

        imageReference.putFile(image)
                .addOnFailureListener(e -> callback.onFailure("Failed to upload your file"))
                .addOnSuccessListener(taskSnapshot -> {
                    getResourceLink(imageReference, new CallBackResponse<String>() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            callback.onSuccess(imageUrl);
                        }

                        @Override
                        public void onError(String message) {
                            callback.onError(message);
                        }

                        @Override
                        public void onFailure(String message) {
                            callback.onFailure(message);
                        }
                    });
                });
    }

    /**
     * @param reference The StorageReference when we uploaded the file
     * @param callback  Returns if we got the link with some states
     */
    private void getResourceLink(StorageReference reference, CallBackResponse<String> callback) {
        if (reference == null) {
            callback.onFailure("There was an unexpected problem while starting the upload");
        } else {
            reference.getDownloadUrl()
                    .addOnFailureListener(e -> callback.onFailure("Failed to communicate with the server"))
                    .addOnSuccessListener(uri -> {
                        if (uri == null) {
                            callback.onError("There was an unexpected problem while getting your image");
                        } else {
                            callback.onSuccess(String.valueOf(uri));
                        }
                    });
        }

    }

    /**
     * @param image The image we want to get the details
     * @return The image name with the type
     */
    private String generateFileName(Uri image) {
        ContentResolver contentResolver = ctx.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.ENGLISH);
        String fileName = dateFormat.format(Calendar.getInstance().getTime());

        if (ContentResolver.SCHEME_CONTENT.equals(image.getScheme())) {
            return String.format(Locale.ENGLISH, "%d_%s.%s", new Random().nextInt(), fileName, mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(image)));
        } else {
            return String.format(Locale.ENGLISH, "%d_%s.%s", new Random().nextInt(), fileName, MimeTypeMap.getFileExtensionFromUrl(image.toString()));
        }
    }
}

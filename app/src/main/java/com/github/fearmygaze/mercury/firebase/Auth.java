package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class Auth {

    private static void validationForm(String email, String username, OnValidationListener listener) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnSuccessListener(result -> {
            if (!Objects.requireNonNull(result.getSignInMethods()).isEmpty()) {
                listener.onResult(false, 1);
            } else {
                FirebaseDatabase.getInstance().getReference().child("users")
                        .orderByChild("username").equalTo("@" + username).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (Objects.equals(snapshot.getChildren().iterator().next().child("username").getValue(String.class), "@" + username)) {
                                        listener.onResult(false, 2);
                                    } else {
                                        listener.onResult(true, 0);
                                    }
                                } else {
                                    listener.onResult(true, 0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onFailure(error.getMessage());
                            }
                        });
            }
        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * @param username Users Username
     * @param name     Users name
     * @param email    Users Email
     * @param password Users password
     * @param image    Users image Uri
     * @param listener Listener Returns <b>True</b> when passes, <b>False</b> when the user return null when getting him and a <b>String</b> when an error occurs
     */
    private static void signUpUser(String username, String name, String email, String password, Uri image, OnResultListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profileImages/" + UUID.randomUUID().toString().trim());
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageRef.putFile(image).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).setPhotoUri(uri).build();
                        user.updateProfile(update).addOnSuccessListener(unused0 -> {
                            user.sendEmailVerification().addOnSuccessListener(unused1 -> {
                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                    usersRef.child(user.getUid()).setValue(new User(user.getUid(), email, "@" + username, name, String.valueOf(uri), token).toMap())
                                            .addOnSuccessListener(unused2 -> listener.onResult(true))
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                            }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                        return;
                    }
                    listener.onResult(false);
                }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
            }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * @param credential Users Email Or Username
     * @param password   Users Password
     * @param context    Context for the Room database
     * @param listener   Listener Returns <b>True</b> when passes, <b>False</b> when the user is not email verified and a <b>String</b> if an error occurs
     */
    private static void signInUser(String credential, String password, Context context, OnResultListener listener) {
        if (credential.contains("@")) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(credential, password)
                    .addOnSuccessListener(authResult -> {
                        if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                            FirebaseDatabase.getInstance().getReference().child("users")
                                    .orderByChild("email")
                                    .equalTo(credential).limitToFirst(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                                AppDatabase.getInstance(context)
                                                        .userDao().insertUser(new User(
                                                                Objects.requireNonNull(userSnapshot.child("userUID").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("email").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("username").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("name").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("imageURL").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("currentNotificationDeviceTokenID").getValue(String.class))
                                                        ));
                                                listener.onResult(true);
                                            } else listener.onResult(false);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure(error.getMessage());
                                        }
                                    });
                            return;
                        }
                        listener.onResult(false);//Didn't find the user
                    }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            FirebaseDatabase.getInstance().getReference().child("users")
                    .orderByChild("username")
                    .equalTo("@" + credential).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(userSnapshot.child("email").getValue(String.class)), password)
                                        .addOnSuccessListener(authResult -> {
                                            if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                                                AppDatabase.getInstance(context).userDao()
                                                        .insertUser(new User(Objects.requireNonNull(userSnapshot.child("userUID").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("email").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("username").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("name").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("imageURL").getValue(String.class)),
                                                                Objects.requireNonNull(userSnapshot.child("currentNotificationDeviceTokenID").getValue(String.class))
                                                        ));
                                                listener.onResult(true);
                                            } else listener.onResult(false);
                                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                            } else listener.onResult(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            listener.onFailure(error.getMessage());
                        }
                    });
        }
    }

    public static void signInForm(String credential, String password, Context context, OnResultListener listener) {
        signInUser(credential, password, context, new OnResultListener() {
            @Override
            public void onResult(boolean result) {
                listener.onResult(result);
            }

            @Override
            public void onFailure(String message) {
                listener.onFailure(message);
            }
        });
    }

    /**
     * @param email          Users email
     * @param emailLayout    email TextInputLayout to set the Errors
     * @param username       Users username
     * @param usernameLayout username TextInputLayout to set the Errors
     * @param name           Users name
     * @param password       Users password
     * @param image          Users Image
     * @param context        Context for getting the String Resource
     * @param listener       Listener Returns <b>True + 0</b> when passes, <b>False + 1</b> when email exists, <b>False + 2</b> when username exists and a <b>String</b> if an error occurs
     */
    public static void signUpForm(String email, TextInputLayout emailLayout, String username, TextInputLayout usernameLayout, String name, String password, Uri image, Context context, OnResultListener listener) {
        validationForm(email, username, new OnValidationListener() {
            @Override
            public void onResult(boolean result, int code) {
                if (result && code == 0) {
                    signUpUser(username, name, email, password, image, new OnResultListener() {
                        @Override
                        public void onResult(boolean result) {
                            listener.onResult(result);
                        }

                        @Override
                        public void onFailure(String message) {
                            listener.onFailure(message);
                        }
                    });
                } else if (!result && code == 1) {
                    Tools.setTimedErrorToLayout(emailLayout, context.getString(R.string.authEmail), true, 5000); //TODO: Remove if i dont like it
                } else if (!result && code == 2) {
                    Tools.setTimedErrorToLayout(usernameLayout, context.getString(R.string.authUsername), true, 5000);
                }
            }

            @Override
            public void onFailure(String message) {
                listener.onFailure(message);
            }
        });
    }

    private interface OnValidationListener {
        void onResult(boolean result, int code);

        void onFailure(String message);
    }

    public interface OnResultListener {
        void onResult(boolean result);

        void onFailure(String message);
    }
}

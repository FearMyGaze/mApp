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

    private static final String BUCKET_USERS = "users";
    private static final String BUCKET_PROFILE_IMAGES = "profileImages/";
    private static final String USER_VALUE_ID = "userUID";
    private static final String USER_VALUE_EMAIL = "email";
    private static final String USER_VALUE_USERNAME = "username";
    private static final String USER_VALUE_NAME = "name";
    private static final String USER_VALUE_IMAGE = "imageURL";
    private static final String USER_VALUE_TOKEN = "notificationToken";
    private static final String USER_VALUE_STATUS = "status";
    private static final String USER_VALUE_LOCATION = "location";
    private static final String USER_VALUE_JOB = "job";
    private static final String USER_VALUE_WEBSITE = "website";
    private static final String USER_VALUE_FRIENDS = "showFriends";
    private static final String USER_VALUE_CREATED = "createdAt";

    /**
     * @param email    Email validation
     * @param username Username validation
     * @param listener Listener Returns <b>1</b> when passes, <b>0</b> when the user return null when getting him and a <b>String</b> when an error occurs
     */
    private static void validationForm(String email, String username, OnResponseListener listener) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnSuccessListener(result -> {
            if (!Objects.requireNonNull(result.getSignInMethods()).isEmpty()) {
                listener.onResult(0);
            } else {
                FirebaseDatabase.getInstance().getReference().child("users")
                        .orderByChild("username").equalTo("@" + username).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (Objects.equals(snapshot.getChildren().iterator().next().child("username").getValue(String.class), "@" + username)) {
                                        listener.onResult(-1);
                                    } else {
                                        listener.onResult(1);
                                    }
                                } else {
                                    listener.onResult(1);
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
     * @param listener Listener Returns <b>1</b> when passes, <b>0</b> when the user return null when getting him and a <b>String</b> when an error occurs
     */
    private static void signUpUser(String username, String name, String email, String password, Uri image, OnResponseListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(BUCKET_PROFILE_IMAGES + UUID.randomUUID().toString().trim());
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS);
        storageRef.putFile(image).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).setPhotoUri(uri).build();
                                user.updateProfile(update).addOnSuccessListener(unused0 -> {
                                    user.sendEmailVerification().addOnSuccessListener(unused1 -> {
                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                            usersRef.child(user.getUid()).setValue(new User(
                                                            user.getUid(),
                                                            email,
                                                            "@" + username,
                                                            name,
                                                            String.valueOf(uri),
                                                            token)
                                                            .toMap(true))
                                                    .addOnSuccessListener(unused2 -> listener.onResult(1))
                                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                    }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                            } else
                                listener.onResult(0);
                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
            }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * @param credential Users Email Or Username
     * @param password   Users Password
     * @param context    Context for the Room database
     * @param listener   Listener Returns <b>1</b> when passes, <b>0</b> when the user is not email verified and a <b>String</b> if an error occurs
     */
    public static void signInUser(String credential, String password, Context context, OnResponseListener listener) {
        if (credential.contains("@")) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(credential, password)
                    .addOnSuccessListener(authResult -> {
                        if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                            FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                    .orderByChild(USER_VALUE_EMAIL)
                                    .equalTo(credential).limitToFirst(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                                AppDatabase.getInstance(context)
                                                        .userDao().insertUser(new User(
                                                                Objects.requireNonNull(userSnapshot.child(USER_VALUE_ID).getValue(String.class)),
                                                                userSnapshot.child(USER_VALUE_EMAIL).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_USERNAME).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_NAME).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_IMAGE).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_TOKEN).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_STATUS).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_LOCATION).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_JOB).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_WEBSITE).getValue(String.class),
                                                                Boolean.TRUE.equals(userSnapshot.child(USER_VALUE_FRIENDS).getValue(Boolean.class)),
                                                                userSnapshot.child(USER_VALUE_CREATED).getValue(Long.class)
                                                        ));
                                                listener.onResult(1);
                                            } else listener.onResult(-1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure(error.getMessage());
                                        }
                                    });
                        } else
                            listener.onResult(-1);
                    }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                    .orderByChild(USER_VALUE_USERNAME)
                    .equalTo("@" + credential).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(userSnapshot.child(USER_VALUE_EMAIL).getValue(String.class)), password)
                                        .addOnSuccessListener(authResult -> {
                                            if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                                                AppDatabase.getInstance(context).userDao()
                                                        .insertUser(new User(
                                                                Objects.requireNonNull(userSnapshot.child(USER_VALUE_ID).getValue(String.class)),
                                                                userSnapshot.child(USER_VALUE_EMAIL).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_USERNAME).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_NAME).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_IMAGE).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_TOKEN).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_STATUS).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_LOCATION).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_JOB).getValue(String.class),
                                                                userSnapshot.child(USER_VALUE_WEBSITE).getValue(String.class),
                                                                Boolean.TRUE.equals(userSnapshot.child(USER_VALUE_FRIENDS).getValue(Boolean.class)),
                                                                userSnapshot.child(USER_VALUE_CREATED).getValue(Long.class)
                                                        ));
                                                listener.onResult(1);
                                            } else listener.onResult(-1);
                                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                            } else listener.onResult(-1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            listener.onFailure(error.getMessage());
                        }
                    });
        }
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
     * @param listener       Listener Returns <b>1</b> when passes, <b>0</b> when email exists, <b>-1</b> when username exists and a <b>String</b> if an error occurs
     */
    public static void signUpForm(String email, TextInputLayout emailLayout, String username, TextInputLayout usernameLayout, String name, String password, Uri image, Context context, OnResponseListener listener) {
        validationForm(email, username, new OnResponseListener() {
            @Override
            public void onResult(int resultCode) {
                switch (resultCode) {
                    case 1:
                        signUpUser(username, name, email, password, image, new OnResponseListener() {
                            @Override
                            public void onResult(int resultCode) {
                                listener.onResult(resultCode);
                            }

                            @Override
                            public void onFailure(String message) {
                                listener.onFailure(message);
                            }
                        });
                        break;
                    case 0:
                        Tools.setTimedErrorToLayout(emailLayout, context.getString(R.string.authEmail), true, 5000);
                        break;
                    case -1:
                        Tools.setTimedErrorToLayout(usernameLayout, context.getString(R.string.authUsername), true, 5000);
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                listener.onFailure(message);
            }
        });
    }

    /**
     * @param context  Context is needed fro inserting data to Room database
     * @param listener Listener returns <b>1</b> when passes, <b>0</b> when the user doesn't exists and -1 when the user isn't email verified
     */
    public static void rememberMe(Context context, OnResponseListener listener) {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (user.isEmailVerified()) {
                    FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                            .orderByChild(USER_VALUE_ID).equalTo(user.getUid()).limitToFirst(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                        AppDatabase.getInstance(context).userDao().updateUser(new User(
                                                Objects.requireNonNull(userSnapshot.child(USER_VALUE_ID).getValue(String.class)),
                                                userSnapshot.child(USER_VALUE_EMAIL).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_USERNAME).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_NAME).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_IMAGE).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_TOKEN).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_STATUS).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_LOCATION).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_JOB).getValue(String.class),
                                                userSnapshot.child(USER_VALUE_WEBSITE).getValue(String.class),
                                                Boolean.TRUE.equals(userSnapshot.child(USER_VALUE_FRIENDS).getValue(Boolean.class)),
                                                userSnapshot.child(USER_VALUE_CREATED).getValue(Long.class))
                                        );
                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                            FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                                    .child(user.getUid())
                                                    .setValue(AppDatabase.getInstance(context).userDao().getUserByUserUID(user.getUid()).toMap(false))
                                                    .addOnSuccessListener(unused -> listener.onResult(1))
                                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                    } else listener.onResult(0);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    listener.onFailure(error.getMessage());
                                }
                            });
                } else
                    listener.onResult(-1);
            } else
                listener.onResult(-1);
        });
    }

    public static void getShowFriends(String senderID, OnResponseListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                .orderByChild(USER_VALUE_ID).equalTo(senderID).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                            if (Boolean.TRUE.equals(userSnapshot.child(USER_VALUE_FRIENDS).getValue(Boolean.class))) {
                                listener.onResult(1);
                            } else listener.onResult(0);
                        } else listener.onResult(-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    public static void setShowFriends(User user, boolean show, OnResponseListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                .child(user.userUID).setValue(new User(
                        user.userUID,
                        user.email,
                        user.username,
                        user.name,
                        user.imageURL,
                        user.notificationToken,
                        user.status,
                        user.location,
                        user.job,
                        user.website,
                        show,
                        user.createdAt
                ).toMap(false))
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void authenticate(String email, String password, Context context, OnResponseListener listener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                        FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                                .orderByChild(USER_VALUE_EMAIL).equalTo(email).limitToFirst(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                            AppDatabase.getInstance(context)
                                                    .userDao().insertUser(new User(
                                                            Objects.requireNonNull(userSnapshot.child(USER_VALUE_ID).getValue(String.class)),
                                                            userSnapshot.child(USER_VALUE_EMAIL).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_USERNAME).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_NAME).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_IMAGE).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_TOKEN).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_STATUS).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_LOCATION).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_JOB).getValue(String.class),
                                                            userSnapshot.child(USER_VALUE_WEBSITE).getValue(String.class),
                                                            Boolean.TRUE.equals(userSnapshot.child(USER_VALUE_WEBSITE).getValue(Boolean.class)),
                                                            userSnapshot.child(USER_VALUE_CREATED).getValue(Long.class)
                                                    ));
                                            listener.onResult(1);
                                        } else listener.onResult(0);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        listener.onFailure(error.getMessage());
                                    }
                                });
                    }
                    listener.onResult(-1);//Didn't find the user
                }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void updatePassword(String password, OnResponseListener listener) {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                .updatePassword(password)
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void updateEmail(String email, OnResponseListener listener) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            fUser.updateEmail(email)
                    .addOnSuccessListener(unused ->
                            fUser.sendEmailVerification()
                                    .addOnSuccessListener(unused1 -> listener.onResult(1))
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                    )
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else listener.onFailure("Failed to find the user");
    }

    public static void deleteAccount(String senderID, OnResponseListener listener) {
        FirebaseDatabase.getInstance().getReference().child(BUCKET_USERS)
                .child(senderID).removeValue()
                .addOnSuccessListener(unused -> listener.onResult(1))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnResponseListener {
        void onResult(int resultCode);

        void onFailure(String message);
    }
}

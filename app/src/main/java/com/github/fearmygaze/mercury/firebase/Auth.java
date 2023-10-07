package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.dao.AuthDao;
import com.github.fearmygaze.mercury.firebase.dao.UserDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import kotlin.NotImplementedError;

public class Auth {

    public static void validateDataAndCreateUser(String username, String email, String password, Context context, OnResponseListener listener) {
        AuthDao.validate(email)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(signInMethodQueryResult -> {
                    List<String> results = signInMethodQueryResult.getSignInMethods();
                    if (results != null && !results.isEmpty()) {
                        listener.onSuccess(1);
                    } else {
                        grantUsername(username, context, new OnResponseListener() {
                            @Override
                            public void onSuccess(int code) {
                                if (code == 0) {
                                    signUp(username, email, password, context, new OnResponseListener() {
                                        @Override
                                        public void onSuccess(int code) {
                                            listener.onSuccess(code);
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            listener.onFailure(message);
                                        }
                                    });
                                } else {
                                    listener.onSuccess(code);
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                listener.onFailure(message);
                            }
                        });
                    }
                });
    }

    private static void grantUsername(String username, Context context, OnResponseListener listener) {
        UserDao.grantUsername(username)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        listener.onSuccess(2);
                    } else {
                        UserDao.writeUsername(username)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0));
                    }
                });
    }

    private static void signUp(String username, String email, String password, Context context, OnResponseListener listener) {
        AuthDao.create(email, password)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused1 -> UserDao.createUser(user.getUid(), username)
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused2 -> listener.onSuccess(0))
                                );
                    } else listener.onFailure("Error contacting the server");
                });
    }

    public static void signIn(String email, String password, Context context, OnDataResponseListener listener) {
        AuthDao.signIn(email, password)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            UserDao.getUserByID(user.getUid())
                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                    .addOnSuccessListener(documentSnapshot -> {
                                        AppDatabase.getInstance(context).userDao().insert(documentSnapshot.toObject(User.class));
                                        listener.onSuccess(0, user.getUid());
                                    });
                        } else {
                            listener.onSuccess(1, null);
                        }
                    } else listener.onSuccess(-1, null);
                });
    }

    public static void sendVerificationEmail(FirebaseUser user, Context context, OnResponseListener listener) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnFailureListener(e -> listener.onFailure("Error sending the verification email"))
                    .addOnSuccessListener(unused -> listener.onSuccess(0));
        } else listener.onSuccess(1);
    }

    public static void sendPasswordResetEmail(String email, Context context, OnResponseListener listener) {
        AuthDao.passwordReset(email)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    public static void rememberMe(FirebaseUser user, Context context, OnUserResponseListener listener) {
        if (user == null) {
            listener.onSuccess(1, null);
        } else if (!user.isEmailVerified()) {
            listener.onSuccess(2, null);
        } else {
            user.reload()
                    .addOnFailureListener(e -> listener.onFailure("Failed to update your user"))
                    .addOnSuccessListener(unused -> FirebaseMessaging.getInstance()
                            .getToken()
                            .addOnFailureListener(e -> listener.onFailure("Failed to update your token"))
                            .addOnSuccessListener(token -> UserDao.getUserByID(user.getUid())
                                    .addOnFailureListener(e -> listener.onFailure("Error getting your user"))
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            UserDao.update(user.getUid(), token)
                                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                    .addOnSuccessListener(unused1 -> listener.onSuccess(0, User.rememberMe(documentSnapshot, token, context)));
                                        } else {
                                            listener.onFailure("Error getting your user");
                                        }
                                    })));
        }
    }

    public static void updatePassword(String password, Context context, OnResponseListener listener) {
        FirebaseUser user = AuthDao.getUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnFailureListener(e -> listener.onFailure("Error updating your password"))
                    .addOnSuccessListener(unused -> listener.onSuccess(0));
        }
    }

    public static void updateEmail(String email, Context context, OnResponseListener listener) {
        FirebaseUser user = AuthDao.getUser();
        if (user != null) {
            user.updateEmail(email)
                    .addOnFailureListener(e -> listener.onFailure("Failed to update your email"))
                    .addOnSuccessListener(unused -> user.sendEmailVerification()
                            .addOnFailureListener(e -> listener.onFailure("Failed to send Verification Email"))
                            .addOnSuccessListener(unused1 -> listener.onSuccess(0))
                    );
        }
    }

    public static void updateNotificationToken(@NonNull FirebaseUser user, String token, Context context) {
        UserDao.update(user.getUid(), token);
    }

    public static void deleteAccount(Context context, OnResponseListener listener) {
        throw new NotImplementedError();
    }

    public static void updateProfile(User user, boolean changed, Uri image, Context context, OnResponseListener listener) {
        if (changed) {
            FireStorage.updateProfileImage(user, image, context, listener);
        } else {
            updateInformation(user, context, listener);
        }
    }

    public static void updateState(String id, boolean state, Context context, OnResponseListener listener) {
        updateInformation(User.updateRoomState(id, state, context), context, listener);
    }

    protected static void updateInformation(User user, Context context, OnResponseListener listener) {
        UserDao.update(user)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> {
                    User.updateRoomUser(user, context);
                    listener.onSuccess(0);
                });
    }

    public static Query searchQuery(String search) {
        if (search.startsWith("loc:") && search.length() >= 7) {
            return UserDao.searchByLocation(search, 40);
        } else if (search.startsWith("job:") && search.length() >= 7) {
            return UserDao.searchByJob(search, 40);
        } else if (search.startsWith("web:") && search.length() >= 7) {
            return UserDao.searchByWeb(search, 40);
        } else {
            return UserDao.searchByUsername(search, 40);
        }
    }

    public static void getUserProfile(String id, Context context, OnUserResponseListener listener) {
        UserDao.getUserByID(id)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        listener.onSuccess(0, documentSnapshot.toObject(User.class));
                    } else listener.onSuccess(1, null);
                });
    }

}

package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.interfaces.OnDataResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.NotImplementedError;

public class Auth {

    public static void validateDataAndCreateUser(String username, String email, String password, Context context, OnResponseListener listener) {
        FirebaseAuth.getInstance()
                .fetchSignInMethodsForEmail(email)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(signInMethodQueryResult -> {
                    List<String> results = signInMethodQueryResult.getSignInMethods();
                    if (results != null && !results.isEmpty()) {
                        listener.onSuccess(1);
                    } else {
                        grantUsername(username, context, new OnDataResponseListener() {
                            @Override
                            public void onSuccess(int code, Object data) {
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

    private static void grantUsername(String username, Context context, OnDataResponseListener listener) {
        FirebaseFirestore.getInstance().collection(User.PUBLIC_DATA)
                .whereEqualTo(User.USERNAME, username)
                .limit(1)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        listener.onSuccess(2, null);
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put(User.USERNAME, username);
                        DocumentReference docReference = FirebaseFirestore.getInstance()
                                .collection(User.PUBLIC_DATA)
                                .document();
                        docReference
                                .set(map)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> listener.onSuccess(0, docReference.getId()));
                    }
                });
    }

    private static void deleteUsername(String reference, Context context, OnResponseListener listener) {
        FirebaseFirestore.getInstance().collection(User.PUBLIC_DATA)
                .document(reference)
                .delete()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> listener.onSuccess(0));
    }

    private static void signUp(String username, String email, String password, Context context, OnResponseListener listener) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(changeRequest)
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                .addOnSuccessListener(unused -> user.sendEmailVerification()
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                        .addOnSuccessListener(unused1 -> FirebaseFirestore.getInstance()
                                                .collection(User.COLLECTION)
                                                .document(user.getUid())
                                                .set(new User(user.getUid(), username))
                                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                .addOnSuccessListener(unused2 -> listener.onSuccess(0)))
                                );
                    } else listener.onFailure("Error contacting the server");
                });
    }

    public static void signIn(String email, String password, Context context, OnDataResponseListener listener) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            FirebaseFirestore.getInstance().collection(User.COLLECTION)
                                    .document(user.getUid())
                                    .get()
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
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
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
                            .addOnSuccessListener(token -> FirebaseFirestore.getInstance()
                                    .collection(User.COLLECTION)
                                    .document(user.getUid())
                                    .get()
                                    .addOnFailureListener(e -> listener.onFailure("Error getting your user"))
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            FirebaseFirestore.getInstance().collection(User.COLLECTION)
                                                    .document(user.getUid())
                                                    .set(User.updateRoomToken(User.convertFromDocumentAndSave(documentSnapshot, context).getId(), token, context))
                                                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                                                    .addOnSuccessListener(unused1 -> listener.onSuccess(0, User.getRoomUser(user.getUid(), context)));
                                        } else {
                                            listener.onFailure("Error getting your user");
                                        }
                                    })));
        }
    }

    public static void updatePassword(String password, Context context, OnResponseListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnFailureListener(e -> listener.onFailure("Error updating your password"))
                    .addOnSuccessListener(unused -> listener.onSuccess(0));
        }
    }

    public static void updateEmail(String email, Context context, OnResponseListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        FirebaseFirestore.getInstance().collection(User.COLLECTION)
                .document(user.getUid())
                .set(User.updateRoomToken(user.getUid(), token, context));
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
        FirebaseFirestore.getInstance().collection(User.COLLECTION)
                .document(user.getId())
                .set(User.updateRoomUser(user, context))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(unused -> {
                    User.updateRoomUser(user, context);
                    listener.onSuccess(0);
                });
    }

    public static Query searchQuery(String search) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(User.COLLECTION);
        String pSearch;
        if (search.startsWith("loc:") && search.length() >= 7) {
            pSearch = search.replace("loc:", "");
            return reference
                    .whereGreaterThanOrEqualTo(User.LOCATION_LOWERED, pSearch.toLowerCase())
                    .whereLessThanOrEqualTo(User.LOCATION_LOWERED, pSearch.toLowerCase() + "\uf8ff")
                    .limit(40);
        } else if (search.startsWith("job:") && search.length() >= 7) {
            pSearch = search.replace("job:", "");
            return reference
                    .whereGreaterThanOrEqualTo(User.JOB_LOWERED, pSearch.toLowerCase())
                    .whereLessThanOrEqualTo(User.JOB_LOWERED, pSearch.toLowerCase() + "\uf8ff")
                    .limit(40);
        } else if (search.startsWith("web:") && search.length() >= 7) {
            pSearch = Tools.addHttp(search.replace("web:", ""));
            return reference
                    .whereGreaterThanOrEqualTo(User.WEB, pSearch)
                    .whereLessThanOrEqualTo(User.WEB, pSearch + "\uf8ff")
                    .limit(40);
        } else {
            return reference
                    .whereGreaterThanOrEqualTo(User.USERNAME_LOWERED, search.toLowerCase())
                    .whereLessThanOrEqualTo(User.USERNAME_LOWERED, search.toLowerCase() + "\uf8ff")
                    .limit(40);
        }
    }

    public static void getUserProfile(String id, Context context, OnUserResponseListener listener) {
        FirebaseFirestore.getInstance().collection(User.COLLECTION)
                .document(id)
                .get()
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        listener.onSuccess(0, documentSnapshot.toObject(User.class));
                    } else listener.onSuccess(1, null);
                });
    }

}

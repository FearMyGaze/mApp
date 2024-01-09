package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;

import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.dao.UserActionsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActions implements UserActionsDao {

    private final FirebaseAuth auth;
    private final FirebaseFirestore database;
    private final Context ctx;

    public UserActions(Context context) {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseFirestore.getInstance();
        this.ctx = context;
    }

    @Override
    public void signUpValidation(String email, String username, String password, SignCallBackResponse<String> callBackResponse) {
        auth.fetchSignInMethodsForEmail(email)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to search for emails"))
                .addOnSuccessListener(signInMethodQueryResult -> {
                    List<String> results = signInMethodQueryResult.getSignInMethods();
                    if (results != null && !results.isEmpty()) {
                        callBackResponse.onError(1, "Email already exists");
                    } else {
                        grantUsername(username, new CallBackResponse<String>() {
                            @Override
                            public void onSuccess(String object) {
                                signUp(email, username, password, new CallBackResponse<String>() {
                                    @Override
                                    public void onSuccess(String object) {
                                        callBackResponse.onSuccess(object);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        callBackResponse.onError(0, message);
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        callBackResponse.onFailure(message);
                                    }
                                });
                            }

                            @Override
                            public void onError(String message) {
                                callBackResponse.onError(2, message);
                            }

                            @Override
                            public void onFailure(String message) {
                                callBackResponse.onFailure(message);
                            }
                        });
                    }
                });
    }

    @Override
    public void grantUsername(String username, CallBackResponse<String> callBackResponse) {
        database.collection("publicData")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to search for the usernames"))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        callBackResponse.onError("Username already exists");
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("username", username);
                        database.collection("publicData")
                                .document()
                                .set(map)
                                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to write the username"))
                                .addOnSuccessListener(unused -> callBackResponse.onSuccess(""));
                    }
                });
    }

    @Override
    public void signUp(String email, String username, String password, CallBackResponse<String> callBackResponse) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to register the user"))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        verificationEmail(new CallBackResponse<String>() {
                            @Override
                            public void onSuccess(String object) {
                                database.collection("users")
                                        .document(user.getUid())
                                        .set(new User(user.getUid(), username))
                                        .addOnFailureListener(e -> callBackResponse.onFailure("Failed to create the user"))
                                        .addOnSuccessListener(unused -> callBackResponse.onSuccess("Your user created, now pls go to your email and activate your account"));
                            }

                            @Override
                            public void onError(String message) {
                                callBackResponse.onError(message);
                            }

                            @Override
                            public void onFailure(String message) {
                                callBackResponse.onFailure(message);
                            }
                        });
                    } else {
                        callBackResponse.onError("Failed to contact the server");
                    }
                });
    }

    @Override
    public void signIn(String email, String password, SignCallBackResponse<String> callBackResponse) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> callBackResponse.onFailure("We couldn't find a user with the credentials you gave us"))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            getUserByID(user.getUid(), new CallBackResponse<User>() {
                                @Override
                                public void onSuccess(User object) {
                                    AppDatabase.getInstance(ctx).userDao().insert(object);
                                    Tools.createSettingsPreference(user.getUid(), ctx);
                                    callBackResponse.onSuccess("You have successfully signed in!");
                                }

                                @Override
                                public void onError(String message) {
                                    callBackResponse.onError(0, message);
                                }

                                @Override
                                public void onFailure(String message) {
                                    callBackResponse.onFailure(message);
                                }
                            });
                        } else {
                            callBackResponse.onError(1, "You need to activate your account before you signIn (Look at your emails to activate it)");
                        }
                    }
                });
    }

    @Override
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void rememberMe(CallBackResponse<User> callBackResponse) {
        FirebaseUser fireUser = auth.getCurrentUser();
        if (fireUser == null) {
            callBackResponse.onError("Something unexpected happened");
        } else if (!fireUser.isEmailVerified()) {
            callBackResponse.onError("Looks like your account isn't verified");
        } else {
            fireUser.reload()
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to reload your user"))
                    .addOnSuccessListener(unused -> FirebaseMessaging.getInstance()
                            .getToken()
                            .addOnFailureListener(e -> callBackResponse.onFailure(""))
                            .addOnSuccessListener(token -> getUserByID(fireUser.getUid(),
                                    new CallBackResponse<User>() {
                                        @Override
                                        public void onSuccess(User user) {
                                            user.setNotificationToken(token);
                                            updateProfileInfo(user, new CallBackResponse<String>() {
                                                @Override
                                                public void onSuccess(String object) {
                                                    callBackResponse.onSuccess(user);
                                                }

                                                @Override
                                                public void onError(String message) {
                                                    callBackResponse.onFailure(message);
                                                }

                                                @Override
                                                public void onFailure(String message) {
                                                    callBackResponse.onFailure(message);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String message) {
                                            callBackResponse.onFailure(message);
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            callBackResponse.onFailure(message);
                                        }
                                    })
                            ));
        }
    }

    @Override
    public void deleteAccount(CallBackResponse<String> callBackResponse) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to delete your user"))
                    .addOnSuccessListener(unused -> callBackResponse.onSuccess("You account has been deleted"));
        } else {
            callBackResponse.onFailure("Failed to get the user");
        }
    }

    @Override
    public void verificationEmail(CallBackResponse<String> callBackResponse) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to send the email"))
                    .addOnSuccessListener(unused -> callBackResponse.onSuccess(""));
        } else {
            callBackResponse.onFailure("Failed to get the user");
        }
    }

    @Override
    public void passwordReset(String email, CallBackResponse<String> callBackResponse) {
        auth.sendPasswordResetEmail(email)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to send the email"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Email send successfully"));
    }

    @Override
    public void updateEmail(String email, CallBackResponse<String> callBackResponse) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updateEmail(email)
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to update your email"))
                    .addOnSuccessListener(unused -> callBackResponse.onSuccess("Email updated successfully"));
        } else {
            callBackResponse.onError("Error: we couldn't get your user data");
        }
    }

    @Override
    public void updatePassword(String password, CallBackResponse<String> callBackResponse) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnFailureListener(e -> callBackResponse.onFailure("Failed to update your password"))
                    .addOnSuccessListener(unused -> callBackResponse.onSuccess("Password updated successfully"));
        } else {
            callBackResponse.onError("Error: we couldn't get your user data");
        }
    }

    public void updateProfile(User user, Uri image, boolean imageChanged, CallBackResponse<String> callBackResponse) {
        if (imageChanged) {
            updateProfileWithImage(user, image, callBackResponse);
        } else {
            updateProfileInfo(user, callBackResponse);
        }
    }

    @Override
    public void updateProfileWithImage(User user, Uri image, CallBackResponse<String> callBackResponse) {
        StorageActions actions = new StorageActions(ctx);
        actions.uploadFile(image, actions.generateProfileImageName(image),
                "profileImages/", new CallBackResponse<String>() {
                    @Override
                    public void onSuccess(String imageLink) {
                        user.setImage(imageLink);
                        updateProfileInfo(user, callBackResponse);
                    }

                    @Override
                    public void onError(String message) {
                        callBackResponse.onError(message);
                    }

                    @Override
                    public void onFailure(String message) {
                        callBackResponse.onFailure(message);
                    }
                });
    }

    @Override
    public void updateProfileInfo(User user, CallBackResponse<String> callBackResponse) {
        database.collection("users")
                .document(user.getId())
                .update("status", user.getStatus(),
                        "location", user.getLocation(),
                        "locationL", user.getLocationL(),
                        "job", user.getJob(),
                        "jobL", user.getJobL(),
                        "website", user.getWebsite(),
                        "notificationToken", user.getNotificationToken())
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to update your information"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Your info updated successfully"));
    }

    @Override
    public void updateNotificationToken(String myUserID, String token) {
        database.collection("users")
                .document(myUserID)
                .update("notificationToken", token)
                .addOnSuccessListener(unused -> User.updateRoomToken(myUserID, token, ctx));
    }

    @Override
    public void updateProfileVisibility(String myUserID, boolean visibility, CallBackResponse<String> callBackResponse) {
        database.collection("users")
                .document(myUserID)
                .update("profileOpen", visibility)
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to update your visibility settings"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Your visibility settings has been updated"));
    }

    @Override
    public void getUserByID(String userID, CallBackResponse<User> callBackResponse) {
        database.collection("users")
                .document(userID)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the user"))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callBackResponse.onSuccess(user);
                        } else {
                            callBackResponse.onError("Something unexpected happened");
                        }
                    } else {
                        callBackResponse.onError("Failed to get the user.");
                    }
                });
    }

    @Override
    public void search(String input, CallBackResponse<List<User>> callBackResponse) {
        Query query;
        if (input.startsWith("loc:") && input.length() >= 7) {
            input = input.replace("loc:", "").toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("locationL", input)
                    .whereLessThanOrEqualTo("locationL", input + "\uf8ff");
        } else if (input.startsWith("job:") && input.length() >= 7) {
            input = input.replace("job:", "").toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("locationL", input)
                    .whereLessThanOrEqualTo("locationL", input + "\uf8ff");
        } else if (input.startsWith("web:") && input.length() >= 7) {
            input = input.replace("web:", "");
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("website", input)
                    .whereLessThanOrEqualTo("website", input + "\uf8ff");
        } else {
            input = input.toLowerCase();
            query = database.collection("users")
                    .whereGreaterThanOrEqualTo("usernameL", input)
                    .whereLessThanOrEqualTo("usernameL", input + "\uf8ff");
        }
        query.limit(40)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to search for users"))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            users.add(document.toObject(User.class));
                        }
                        callBackResponse.onSuccess(users);
                    } else {
                        callBackResponse.onError("We didn't find users with the given criteria");
                    }
                });
    }

}

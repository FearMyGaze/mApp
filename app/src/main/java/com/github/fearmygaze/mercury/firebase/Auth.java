package com.github.fearmygaze.mercury.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Auth {

    private final Context context;
    private final FirebaseAuth fireAuth;
    private final FirebaseFirestore fireStore;

    /**
     * @param ctx Use only for translation and we create an instance for FirebaseAuth and FirebaseFireStore
     */
    public Auth(Context ctx) {
        this.context = ctx;
        this.fireAuth = FirebaseAuth.getInstance();
        this.fireStore = FirebaseFirestore.getInstance();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sign Up/In/Out
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param email    The email of the user we want to send to firebase
     * @param username The username of the user we want to send to firebase
     * @param password The password of the user we want to send to firebase
     * @param response The return of the firebase response
     */
    public void signUp(String email, String username, String password, SignCallBackResponse<String> response) {
        if (email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.onFailure("Values cannot be empty");
            return;
        }
        FirebaseFunctions.getInstance("europe-west1")
                .getHttpsCallable("signUpFlow")
                .call(new HashMap<String, String>() {{
                    put("email", email.trim());
                    put("username", username.trim());
                    put("password", password.trim());
                }})
                .addOnFailureListener(e -> response.onFailure(e.getMessage()))
                .addOnSuccessListener(result -> {
                    if (result.getData() != null) {
                        try {
                            JSONObject jsonObj = new JSONObject((String) result.getData());
                            switch (jsonObj.getString("code")) {
                                case "100":
                                    response.onError(1, "Either your email is wrong formatted or there was a problem while creating your account");
                                    break;
                                case "101":
                                    response.onError(2, "Your username has the wrong format");
                                    break;
                                case "102":
                                    response.onError(3, "Your password has the wrong format");
                                    break;
                                case "103":
                                    response.onError(2, "The username already exists");
                                    break;
                                case "200":
                                    response.onSuccess("You have successfully created your account. The first time you sign in we are going to send you a verification email to activate your account");
                                    break;
                                default:
                                    response.onFailure("Failed to receive data from the server you are either running an older version of the app or something broke from our end!");
                                    break;
                            }
                        } catch (JSONException e) {
                            response.onFailure("Failed to receive data from the server you are either running an older version of the app or something broke from our end!");
                        }
                    }
                });
    }

    /**
     * @param email    The email of the user we want to send to firebase
     * @param password The password of the user we want to send to firebase
     * @param response The return of the firebase response
     */
    public void signIn(String email, String password, SignCallBackResponse<String> response) {
        fireAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> response.onFailure("There is no account with the parameters you gave us"))
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fireUser = authResult.getUser();
                    if (fireUser == null) {
                        response.onFailure("We couldn't find your user");
                    } else if (!fireUser.isEmailVerified()) {
                        emailVerification(fireUser, new CallBackResponse<String>() {
                            @Override
                            public void onSuccess(String message) {
                                response.onSuccess(message);
                            }

                            @Override
                            public void onError(String message) {
                                response.onError(0, message);
                            }

                            @Override
                            public void onFailure(String message) {
                                response.onFailure(message);
                            }
                        });
                    } else {
                        new Search(context).getUserById(fireUser.getUid(),
                                new CallBackResponse<User1>() {
                                    @Override
                                    public void onSuccess(User1 user) {
                                        RoomDB.getInstance(context).users().insert(user);
                                        Tools.createSettingsPreference(user.getId(), context);
                                        response.onSuccess("Successfully signed in");
                                    }

                                    @Override
                                    public void onError(String message) {
                                        response.onError(0, "There was an error getting your user please wait 1-5 minutes and try again");
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        response.onFailure(message);
                                    }
                                });
                    }
                });
    }

    /**
     * @param deleteLocal If we want to delete the user from inside the Room database
     */
    public void signOut(boolean deleteLocal) {
        FirebaseUser user = fireAuth.getCurrentUser();
        if (user == null) return;

        if (deleteLocal) {
            RoomDB.getInstance(context).users().delete(user.getUid());
        }

        fireAuth.signOut();
    }

    /**
     * @param user     The FirebaseUser we want to send the email verification
     * @param response The return of the firebase response
     */
    public void emailVerification(FirebaseUser user, CallBackResponse<String> response) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnFailureListener(e -> response.onFailure("Failed to send the email"))
                    .addOnSuccessListener(unused -> response.onSuccess("We have sent you an activation email in your given email"));
        } else {
            response.onFailure("Failed to get the user");
        }
    }

    /**
     * @param response The return of the firebase response
     */
    public void rememberMe(CallBackResponse<User1> response) {
        fireAuth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d("customLog", "Auth.java:rememberMe:Line:184" + "user==null ??");
                response.onError("Error while getting your credentials");
            } else if (!user.isEmailVerified()) {
                signOut(false);
                response.onError("You need to activate your account to continue");
            } else {
                user.reload()
                        .addOnFailureListener(e -> response.onFailure("Failed to reload the user"))
                        .addOnSuccessListener(unused -> {
                            new Search(context).getUserById(user.getUid(), new CallBackResponse<User1>() {
                                @Override
                                public void onSuccess(User1 fetchedUser) {
                                    response.onSuccess(fetchedUser);
                                    updateNotificationToken(user.getUid(), new CallBackResponse<String>() {
                                        @Override
                                        public void onSuccess(String empty) {

                                        }

                                        @Override
                                        public void onError(String message) {
                                            signOut(false);
                                            response.onError("Failed to update you user");
                                        }

                                        @Override
                                        public void onFailure(String message) {
                                            signOut(false);
                                            response.onFailure("Failed to update your user");
                                        }
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    signOut(false);
                                    response.onError(message);
                                }

                                @Override
                                public void onFailure(String message) {
                                    signOut(false);
                                    response.onFailure(message);
                                }
                            });
                        });
            }
        });
    }

    /**
     * @param email    The users email we want to send the password reset
     * @param response The return of the firebase response
     */
    public void forgotPassword(String email, CallBackResponse<String> response) {
        //TODO: We need to figure out if we want to implement it or not support it!!
        response.onSuccess("Not Implemented yet");
    }

    /**
     * @param response The return of the firebase response
     */
    public void deleteAccount(CallBackResponse<String> response) {
        FirebaseUser user = fireAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnFailureListener(e -> response.onFailure("Failed to delete your user"))
                    .addOnSuccessListener(unused -> {
                        //TODO we need to delete local user
                        response.onSuccess("You account has been deleted");
                    });
        } else {
            response.onError("Something happened");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Updates
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param password The new password of the current user
     * @param response The return of the firebase response
     */
    public void changePassword(String password, CallBackResponse<String> response) {
        FirebaseUser user = fireAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnFailureListener(e -> response.onFailure("Failed to update your password"))
                    .addOnSuccessListener(unused -> response.onSuccess("Password updated successfully"));
        } else {
            response.onError("Error: we couldn't get your user data");
        }
    }

    /**
     * @param email    The new email of the current user
     * @param response The return of the firebase response
     */
    public void changeEmail(String email, CallBackResponse<String> response) {
        FirebaseUser user = fireAuth.getCurrentUser();
        if (user != null) {//TODO: We need to see if this works
            user.verifyBeforeUpdateEmail(email)
                    .addOnFailureListener(e -> response.onFailure("Failed to update your email"))
                    .addOnSuccessListener(unused -> response.onSuccess("Email updated successfully"));
        } else {
            response.onError("There was an internal error (Somewhere we didn't even know)");
        }
    }

    /**
     * @param imagePath The image URI path (from the device)
     * @param user      The updated user we want to send in firebase
     * @param response  The return of the firebase return
     */
    public void updateProfile(Uri imagePath, User1 user, CallBackResponse<String> response) {
        if (imagePath != null) {
            new Storage(context).uploadProfileImage(imagePath, new CallBackResponse<String>() {
                @Override
                public void onSuccess(String imageUrl) {
                    user.setImage(imageUrl);
                    updateProfilePartial(user, response);
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
        } else {
            updateProfilePartial(user, response);
        }
    }

    /**
     * @param user     The updated user (without uploading the new image) we want to send in firebase
     * @param response The return of the firebase response
     */
    private void updateProfilePartial(User1 user, CallBackResponse<String> response) {
        fireStore.collection("users")
                .document(user.getId())
                .update("bio", user.getBio(),
                        "image", user.getImage(),
                        "username", user.getUsername(),
                        "usernameL", user.getUsernameL(),
                        "location", user.getLocation(),
                        "locationL", user.getLocationL(),
                        "job", user.getJob(),
                        "jobL", user.getJobL(),
                        "website", user.getWebsite(),
                        "profileOpen", user.isProfileOpen(),
                        "notificationToken", user.getNotificationToken())
                .addOnFailureListener(e -> response.onFailure("Failed to update your information"))
                .addOnSuccessListener(unused -> {
                    RoomDB.getInstance(context).users().insert(user);
                    response.onSuccess("Your info updated successfully");
                });
    }

    /**
     * @param userId The users id we want to update
     * @param token  The new token we want to update
     */
    public void updateNotificationToken(String userId, String token) {
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(nToken -> {
                    if (nToken != null) {
                        fireStore.collection("users")
                                .document(userId)
                                .update("notificationToken", token)
                                .addOnSuccessListener(unused -> RoomDB.getInstance(context).users().updateToken(token, userId));
                    }
                });
    }

    /**
     * @param userId   The users id we want to update
     * @param response The return of the firebase response
     */
    public void updateNotificationToken(String userId, CallBackResponse<String> response) {
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnFailureListener(e -> response.onFailure("Failed to get the notification token"))
                .addOnSuccessListener(nToken -> {
                    if (nToken != null) {
                        fireStore.collection("users")
                                .document(userId)
                                .update("notificationToken", nToken)
                                .addOnFailureListener(e -> response.onFailure("Failed to update your data"))
                                .addOnSuccessListener(unused -> response.onSuccess(User.updateRoomToken(userId, nToken, context).getNotificationToken()));
                    }
                    //TODO: We need to implement a better way to fix the onSuccess
                });
    }

    /**
     * @param userId   The users id we want to update
     * @param b        The boolean value of profileVisibility
     * @param response The return of the firebase response
     */
    public void updateProfileVisibility(String userId, boolean b, CallBackResponse<String> response) {
        fireStore.collection("users")
                .document(userId)
                .update("profileOpen", b)
                .addOnFailureListener(e -> response.onFailure("Failed to update your user"))
                .addOnSuccessListener(unused -> response.onSuccess("Your visibility settings has been updated"));
    }

}

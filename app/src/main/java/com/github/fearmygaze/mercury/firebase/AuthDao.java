package com.github.fearmygaze.mercury.firebase;

import android.net.Uri;

import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.google.firebase.auth.FirebaseUser;

public interface AuthDao {
    /**
     * @param email    The email of the user we want to send to firebase
     * @param username The username of the user we want to send to firebase
     * @param password The password of the user we want to send to firebase
     * @param response The return of the firebase response
     */
    void signUp(String email, String password, String username, SignCallBackResponse<String> response);

    /**
     * @param email    The email of the user we want to send to firebase
     * @param password The password of the user we want to send to firebase
     * @param response The return of the firebase response
     */
    void signIn(String email, String password, CallBackResponse<String> response);

    /**
     * @param deleteLocal If we want to delete the user from inside the Room database
     */
    void signOut(boolean deleteLocal);

    /**
     * @param response The return of the firebase response
     */
    void deleteAccount(CallBackResponse<String> response);

    void rememberMe(CallBackResponse<User1> response);

    /**
     * @param imagePath The image URI path (from the device)
     * @param user      The updated user we want to send in firebase
     * @param response  The return of the firebase return
     */
    void updateProfile(Uri imagePath, User1 user, CallBackResponse<String> response);

    /**
     * @param firebaseUser The FirebaseUser we want to send the email verification
     * @param response     The return of the firebase response
     */
    void verifyEmail(FirebaseUser firebaseUser, CallBackResponse<String> response);

    /**
     * @param email    The new email of the current user
     * @param response The return of the firebase response
     */
    void changeEmail(String email, CallBackResponse<String> response);

    /**
     * @param password The new password of the current user
     * @param response The return of the firebase response
     */
    void changePassword(String password, CallBackResponse<String> response);

    /**
     * @param userId The users id we want to update
     * @param token  The new token we want to update
     */
    void updateMessagingToken(String userId, String token);

    /**
     * @param userId   The users id we want to update
     * @param response The return of the firebase response
     */
    void updateMessagingToken(String userId, CallBackResponse<String> response);


}

package com.github.fearmygaze.mercury.firebase.dao;

import android.net.Uri;

import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.SignCallBackResponse;
import com.github.fearmygaze.mercury.model.User;

import java.util.List;

public interface UserActionsDao {

    ///////////////////////////////////////////////////////////////////////////
    // SignIn / SignUp / SignOut
    ///////////////////////////////////////////////////////////////////////////

    void signUp(String email, String username, String password, SignCallBackResponse<String> callBackResponse);

    void signIn(String email, String password, SignCallBackResponse<String> callBackResponse);

    void signOut();

    void rememberMe(CallBackResponse<User> callBackResponse);

    void deleteAccount(CallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Forgot
    ///////////////////////////////////////////////////////////////////////////

    void verificationEmail(CallBackResponse<String> callBackResponse);

    void passwordReset(String email, CallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // Updates
    ///////////////////////////////////////////////////////////////////////////

    void updateEmail(String email, CallBackResponse<String> callBackResponse);

    void updatePassword(String password, CallBackResponse<String> callBackResponse);

    void updateProfileWithImage(User user, Uri image, CallBackResponse<String> callBackResponse);

    void updateProfileInfo(User user, CallBackResponse<String> callBackResponse);

    void updateNotificationToken(String myUserID, String token);

    void updateProfileVisibility(String myUserID, boolean visibility, CallBackResponse<String> callBackResponse);

    ///////////////////////////////////////////////////////////////////////////
    // General
    ///////////////////////////////////////////////////////////////////////////

    void getUserByID(String userID, CallBackResponse<User> callBackResponse);

    void getUserByUsername(String username, CallBackResponse<User> callBackResponse);

    void search(String query, CallBackResponse<List<User>> callBackResponse);
}

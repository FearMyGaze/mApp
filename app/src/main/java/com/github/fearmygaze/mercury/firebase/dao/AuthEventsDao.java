package com.github.fearmygaze.mercury.firebase.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class AuthEventsDao {

    private static FirebaseAuth INSTANCE = null;

    public static synchronized FirebaseAuth getInstance() {
        return INSTANCE = (INSTANCE == null) ? FirebaseAuth.getInstance() : INSTANCE;
    }

    public static Task<SignInMethodQueryResult> validate(String email) {
        return getInstance().fetchSignInMethodsForEmail(email);
    }

    public static Task<AuthResult> create(String email, String password) {
        return getInstance().createUserWithEmailAndPassword(email, password);
    }

    public static Task<AuthResult> signIn(String email, String password) {
        return getInstance().signInWithEmailAndPassword(email, password);
    }

    public static Task<Void> passwordReset(String email) {
        return getInstance().sendPasswordResetEmail(email);
    }

    public static FirebaseUser getUser() {
        return getInstance().getCurrentUser();
    }

    public static void signOut() {
        getInstance().signOut();
    }
}

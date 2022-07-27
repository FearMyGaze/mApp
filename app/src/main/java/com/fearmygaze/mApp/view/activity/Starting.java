package com.fearmygaze.mApp.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.fragment.SignIn;
import com.fearmygaze.mApp.view.fragment.SignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Starting extends AppCompatActivity {

    public Fragment signIn, signUp;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        user = FirebaseAuth.getInstance().getCurrentUser();

        signIn = new SignIn();
        signUp = new SignUp();

        if (user != null) {// TODO: Fix the user that is not enabled to not enter the app
            startActivity(new Intent(this, Main.class));
            finish();
        }else{
            replaceFragment(signIn);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.startingFrame, fragment);
        fragmentTransaction.commit();
    }

    public Fragment reInitiateFragmentSignIn(){
        return signIn = new SignIn();
    }

    public Fragment reInitiateFragmentSignUp(){
        return signUp = new SignUp();
    }
}
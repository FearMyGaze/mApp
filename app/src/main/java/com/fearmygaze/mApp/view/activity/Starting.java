package com.fearmygaze.mApp.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.fragment.SignIn;
import com.fearmygaze.mApp.view.fragment.SignUp;

public class Starting extends AppCompatActivity {

    public Fragment signIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        signIn = new SignIn();
        signUp = new SignUp();

        replaceFragment(signIn);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.startingFrame, fragment).commit();
    }

    public Fragment reInitiateFragmentSignIn() {
        return signIn = new SignIn();
    }

    public Fragment reInitiateFragmentSignUp() {
        return signUp = new SignUp();
    }
}
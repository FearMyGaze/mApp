package com.github.fearmygaze.mercury.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.view.fragment.SignIn;
import com.github.fearmygaze.mercury.view.fragment.SignUp;

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
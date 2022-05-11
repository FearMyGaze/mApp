package com.fearmygaze.mApp.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}
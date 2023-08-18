package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.view.fragment.Loading;
import com.github.fearmygaze.mercury.view.fragment.SignIn;

public class Starting extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        if (AppDatabase.getInstance(Starting.this).userDao().getAllUsers().size() > 0) {
            replaceFragment(Loading.newInstance());
        } else {
            replaceFragment(SignIn.newInstance());
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.startingFrame, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }
}

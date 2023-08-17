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
import com.github.fearmygaze.mercury.view.fragment.SignUp;

public class Starting extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        /*
         * TODO: We need to check if the room has saved users
         *       if yes then start the AuthStateListener
         *           if that pass the Main else SignIn
         *       if not show the signUp
         * */
        if (AppDatabase.getInstance(Starting.this).userDao().getAllUsers().size() > 0) {
            replaceFragment(Loading.newInstance(), getSupportFragmentManager());
        }else {
            replaceFragment(SignUp.newInstance(), getSupportFragmentManager());
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void replaceFragment(Fragment fragment, FragmentManager manager) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.startingFrame, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }
}

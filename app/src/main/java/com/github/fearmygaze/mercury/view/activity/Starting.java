package com.github.fearmygaze.mercury.view.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.fearmygaze.mercury.BuildConfig;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.AuthTokenService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Starting extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(AuthTokenService.JOB_ID, new ComponentName(Starting.this, AuthTokenService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(interval())
                .build();//TODO: Put this inside the AuthTokenRefresh
        jobScheduler.schedule(jobInfo);

        List<User1> users = RoomDB.getInstance(Starting.this).users().getAll();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (users == null || users.isEmpty()) {
                finish();
                startActivity(new Intent(Starting.this, Welcome.class));
            } else if (user == null || !user.isEmailVerified()) {
                finish();
                startActivity(new Intent(Starting.this, Welcome.class));
            } else {
                new Handler().postDelayed(() -> {
                    finish();
                    startActivity(new Intent(Starting.this, Main.class));
                }, 1234);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    private static int interval() {
        if (BuildConfig.DEBUG) {
            return 900_000; // 15min
        } else {
            return 1_800_000; // 30min
        }
    }
}

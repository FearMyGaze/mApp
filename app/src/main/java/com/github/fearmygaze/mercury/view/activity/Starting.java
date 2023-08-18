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
import com.github.fearmygaze.mercury.firebase.AuthRefresh;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Starting extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(123, new ComponentName(Starting.this, AuthRefresh.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(interval())
                .build();
        jobScheduler.schedule(jobInfo);

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                finish();
                startActivity(new Intent(Starting.this, SignIn.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (!user.isEmailVerified()) {
                finish();
                startActivity(new Intent(Starting.this, SignIn.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                new Handler().postDelayed(() -> {
                    finish();
                    startActivity(new Intent(Starting.this, Main.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }, 1000);
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
            return 900_000;
        } else {
            return 1_800_000;
        }
    }
}

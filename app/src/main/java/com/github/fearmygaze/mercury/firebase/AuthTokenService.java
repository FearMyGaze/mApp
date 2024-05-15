package com.github.fearmygaze.mercury.firebase;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthTokenService extends JobService {

    public static final int JOB_ID = 123;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) user.getIdToken(true);
        });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

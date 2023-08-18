package com.github.fearmygaze.mercury.view.fragment;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.fearmygaze.mercury.BuildConfig;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.AuthRefresh;
import com.github.fearmygaze.mercury.view.activity.Main;
import com.github.fearmygaze.mercury.view.activity.Starting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loading extends Fragment {

    View view;

    public Loading() {

    }

    public static Loading newInstance() {
        return new Loading();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loading, container, false);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                ((Starting) requireActivity()).replaceFragment(SignIn.newInstance());
            } else {
                JobScheduler jobScheduler = (JobScheduler) view.getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = new JobInfo.Builder(123, new ComponentName(view.getContext(), AuthRefresh.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(interval())
                        .build();
                jobScheduler.schedule(jobInfo);
                startActivity(new Intent(view.getContext(), Main.class));
                requireActivity().finish();
            }

        });
        return view;
    }


    private static int interval() {
        if (BuildConfig.DEBUG) {
            return 900_000;
        } else {
            return 1_800_000;
        }
    }
}

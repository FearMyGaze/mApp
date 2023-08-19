package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;

public class Notifications extends Fragment {

    View view;
    User user;

    public Notifications() {
    }

    public static Notifications newInstance(User user) {
        Notifications notifications = new Notifications();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        notifications.setArguments(bundle);
        return notifications;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);


        return view;
    }
}

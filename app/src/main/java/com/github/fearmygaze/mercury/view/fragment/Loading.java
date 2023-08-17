package com.github.fearmygaze.mercury.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.view.activity.Main;
import com.google.firebase.auth.FirebaseAuth;

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
        User user = new User();
        user.setUsername("nick");
        PrivatePreference preference = new PrivatePreference(view.getContext());
        preference.putString("currentUser", user.getUsername());

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {

        });

        requireActivity().startActivity(new Intent(view.getContext(), Main.class));
        requireActivity().finish();
        return view;
    }
}

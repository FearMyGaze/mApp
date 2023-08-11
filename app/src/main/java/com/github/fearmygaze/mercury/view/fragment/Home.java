package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Home extends Fragment {

    View view;
    String id;

    User user;

    RecyclerView recyclerView;
    FloatingActionButton create;

    public Home() {

    }

    public static Home newInstance(User user) {
        Home home = new Home();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        home.setArguments(bundle);
        return home;
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.homeRecycler);
        create = view.findViewById(R.id.homeCreate);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

package com.github.fearmygaze.mercury.view.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriends;

public class TabFriends extends Fragment {
    View view;
    User user;

    AdapterFriends adapterFriends;
    FirestoreRecyclerOptions<Request> options;

    public TabFriends() {
    }

    public static TabFriends newInstance(User user) {
        TabFriends tabFriends = new TabFriends();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        tabFriends.setArguments(bundle);
        return tabFriends;
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
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        SwipeRefreshLayout swipe = view.findViewById(R.id.fragmentFriendsSwipe);
        RecyclerView recyclerView = view.findViewById(R.id.fragmentFriendsRecycler);

        options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(user), Request.class)
                .setLifecycleOwner(this)
                .build();

        adapterFriends = new AdapterFriends(user, options);
        recyclerView.setAdapter(adapterFriends);
        recyclerView.setLayoutManager(new CustomLinearLayout(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(null);

        swipe.setOnRefreshListener(() -> {
            fetch(user);
            swipe.setRefreshing(false);
        });

        return view;
    }

    public void fetch(User user) {
        adapterFriends.updateOptions(new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(Friends.friendsQuery(user), Request.class)
                .setLifecycleOwner(this)
                .build());
    }
}
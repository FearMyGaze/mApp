package com.github.fearmygaze.mercury.view.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.FriendController;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.interfaces.IFriend;
import com.github.fearmygaze.mercury.model.Friend;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriendList;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends Fragment {
    View view;

    SwipeRefreshLayout refreshLayout;
    MaterialTextView friendError;
    RecyclerView friendRecycler;

    List<Friend> friendLists;
    AdapterFriendList adapterFriendList;

    PrivatePreference preference;
    AppDatabase database;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        preference = new PrivatePreference(requireContext());
        database = AppDatabase.getInstance(requireContext());
        user = database.userDao().getUserByID(preference.getInt("id"));

        refreshLayout = view.findViewById(R.id.friendListUpdate);
        friendError = view.findViewById(R.id.friendListError);
        friendRecycler = view.findViewById(R.id.friendListRecycler);

        friendLists = new ArrayList<>();
        adapterFriendList = new AdapterFriendList(friendLists, user);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRecycler.setLayoutManager(layoutManager);
        friendRecycler.setAdapter(adapterFriendList);

        FriendController.showFriends(user, adapterFriendList.getOffset(), requireContext(), new IFriend() {
            @Override
            public void onSuccess(List<Friend> friendList) {
                adapterFriendList.refillList(friendList);
                friendError.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                adapterFriendList.clearListAndRefreshAdapter();
                friendError.setVisibility(View.VISIBLE);
            }
        });

        refreshLayout.setOnRefreshListener(this::refresh);

        friendRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (dy >= 0 && lastVisibleItemPosition >= adapterFriendList.getItemCount() - 1) {
                    fetchRows();
                }

            }

            private void fetchRows() {
                adapterFriendList.setOffset(adapterFriendList.getOffset() + 10);
                FriendController.showFriends(user, adapterFriendList.getOffset(), getContext(), new IFriend() {
                    @Override
                    public void onSuccess(List<Friend> friendList) {
                        adapterFriendList.addResultAndRefreshAdapter(friendList);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        FriendController.showFriends(user, 0, view.getContext(), new IFriend() {
            @Override
            public void onSuccess(List<Friend> friendList) {
                adapterFriendList.refillList(friendList);
                friendError.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                friendError.setVisibility(View.VISIBLE);
            }
        });
        refreshLayout.setRefreshing(false);
    }
}
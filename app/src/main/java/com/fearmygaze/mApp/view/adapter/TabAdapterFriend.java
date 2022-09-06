package com.fearmygaze.mApp.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.tabs.FriendsList;
import com.fearmygaze.mApp.view.tabs.FriendsRequests;

public class TabAdapterFriend extends FragmentStateAdapter {

    User user;

    public TabAdapterFriend(@NonNull FragmentActivity fragmentActivity, User user) {
        super(fragmentActivity);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FriendsRequests(user);
        }
        return new FriendsList(user);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
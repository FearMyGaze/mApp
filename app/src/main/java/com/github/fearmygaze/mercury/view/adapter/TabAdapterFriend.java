package com.github.fearmygaze.mercury.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.fearmygaze.mercury.view.tabs.FriendList;
import com.github.fearmygaze.mercury.view.tabs.FriendsRequests;

public class TabAdapterFriend extends FragmentStateAdapter {

    public TabAdapterFriend(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FriendsRequests();
        }
        return new FriendList();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
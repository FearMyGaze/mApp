package com.fearmygaze.mApp.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fearmygaze.mApp.view.tabs.FriendList;
import com.fearmygaze.mApp.view.tabs.FriendRequests;

public class TabAdapterFriend extends FragmentStateAdapter {


    public TabAdapterFriend(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FriendRequests();
        }
        return new FriendList(); // position = 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

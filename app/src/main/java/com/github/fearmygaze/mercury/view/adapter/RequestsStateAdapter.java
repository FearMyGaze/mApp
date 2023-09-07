package com.github.fearmygaze.mercury.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.tab.TabFriends;
import com.github.fearmygaze.mercury.view.tab.TabRequests;

public class RequestsStateAdapter extends FragmentStateAdapter {

    User user;
    TabRequests tabRequests;
    TabFriends tabFriends;

    public RequestsStateAdapter(@NonNull FragmentActivity fragmentActivity, @NonNull User user) {
        super(fragmentActivity);
        this.user = user;
        this.tabRequests = TabRequests.newInstance(user);
        this.tabFriends = TabFriends.newInstance(user);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return tabRequests;
        }
        return tabFriends;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public TabRequests getTabPending() {
        return tabRequests;
    }

    public TabFriends getTabFriends() {
        return tabFriends;
    }
}

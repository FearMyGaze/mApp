package com.fearmygaze.mApp.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fearmygaze.mApp.view.tabs.HomeMessages;
import com.fearmygaze.mApp.view.tabs.HomeRequests;

public class TabAdapterHome extends FragmentStateAdapter {

    public TabAdapterHome(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new HomeRequests();
        }
        return new HomeMessages(); // position = 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

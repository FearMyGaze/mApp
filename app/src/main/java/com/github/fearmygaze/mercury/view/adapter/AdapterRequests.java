package com.github.fearmygaze.mercury.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.fearmygaze.mercury.view.tab.FragmentBlocked;
import com.github.fearmygaze.mercury.view.tab.FragmentRequests;

public class AdapterRequests extends FragmentStateAdapter {

    String id;

    public AdapterRequests(@NonNull FragmentActivity fragmentActivity, @NonNull String myID) {
        super(fragmentActivity);
        this.id = myID;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return FragmentBlocked.newInstance(id);
        } else return FragmentRequests.newInstance(id);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

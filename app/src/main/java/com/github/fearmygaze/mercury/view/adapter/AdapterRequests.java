package com.github.fearmygaze.mercury.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.fearmygaze.mercury.view.tab.FragmentBlockedRequest;
import com.github.fearmygaze.mercury.view.tab.FragmentWaitingRequest;

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
            return FragmentBlockedRequest.newInstance(id);
        } else return FragmentWaitingRequest.newInstance(id);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

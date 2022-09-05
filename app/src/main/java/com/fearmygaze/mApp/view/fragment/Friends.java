package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.adapter.TabAdapterFriend;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Friends extends Fragment {

    public Friends(User user) {
        this.user = user;
    }

    View view;

    User user;

    TabLayout tabLayout;
    ViewPager2 pager2;
    TabLayoutMediator mediator;

    /*
     * TODO: We need to add a way to press the back button and go to fragment Chat
     *       to do that we need to add a fragment manager (i think ??)
     * */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        tabLayout = view.findViewById(R.id.friendsTabLayout);
        pager2 = view.findViewById(R.id.friendsViewPager2);

        pager2.setAdapter(new TabAdapterFriend(requireActivity(), user));
        mediator = new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getResources().getText(R.string.friendsTab0));
                    break;
                case 1:
                    tab.setText(getResources().getText(R.string.friendsTab1)); //TODO: Add Number Badges
            }
        });
        mediator.attach();

        return view;
    }
}
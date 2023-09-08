package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.RequestsStateAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class People extends Fragment {

    View view;
    User user;

    TabLayout tabLayout;
    ViewPager2 pager2;
    RequestsStateAdapter stateRequests;

    public People() {

    }

    public static People newInstance(User user) {
        People people = new People();
        Bundle bundle = new Bundle();
        bundle.putParcelable(User.PARCEL, user);
        people.setArguments(bundle);
        return people;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(User.PARCEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        tabLayout = view.findViewById(R.id.peopleTabLayout);
        pager2 = view.findViewById(R.id.peopleViewPager);

        stateRequests = new RequestsStateAdapter(requireActivity(), user);
        pager2.setAdapter(stateRequests);
        new TabLayoutMediator(tabLayout, pager2, ((tab, position) -> {
            if (position == 1) {
                tab.setText(getString(R.string.peopleTabPending));
            } else {
                tab.setText(getString(R.string.peopleTabFriends));
            }
        })).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    stateRequests.getTabFriends().fetch(user);
                } else {
                    stateRequests.getTabPending().fetch(user);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    stateRequests.getTabFriends().fetch(user);
                } else {
                    stateRequests.getTabPending().fetch(user);
                }
            }
        });

        return view;
    }
}

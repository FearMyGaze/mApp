package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.FragmentStateRequests;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class People extends Fragment {

    View view;
    String id;

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentStateRequests stateRequests;
    SwipeRefreshLayout refreshLayout;

    public People() {

    }

    public static People newInstance(String id) {
        People people = new People();
        Bundle bundle = new Bundle();
        bundle.putString(User.ID, id);
        people.setArguments(bundle);
        return people;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(User.ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        refreshLayout = view.findViewById(R.id.peopleRefresh);
        tabLayout = view.findViewById(R.id.peopleTabLayout);
        pager2 = view.findViewById(R.id.peopleViewPager);

        stateRequests = new FragmentStateRequests(requireActivity(), id);
        pager2.setAdapter(stateRequests);
        new TabLayoutMediator(tabLayout, pager2, ((tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText("Pending");
                    return;
                case 2:
                    tab.setText("Blocked");
                    return;
                default:
                    tab.setText("Friends");
            }
        })).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Toast.makeText(view.getContext(), "ReSelected", Toast.LENGTH_SHORT).show();
            }
        });

        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));

        return view;
    }

    public void onRefresh() {
    }
}

package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;
import com.fearmygaze.mApp.view.adapter.TabAdapterFriend;
import com.fearmygaze.mApp.view.adapter.TabAdapterHome;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    View view;

    TabLayout tabLayout;
    ViewPager2 pager2;
    TabLayoutMediator mediator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.homeTabLayout);
        pager2 = view.findViewById(R.id.homeViewPager2);

        pager2.setAdapter(new TabAdapterHome(requireActivity()));
        mediator = new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(getResources().getText(R.string.homeTab0));
                    break;
                case 1:
                    tab.setText(getResources().getText(R.string.homeTab1));
            }
        });
        mediator.attach();



        return view;
    }
}
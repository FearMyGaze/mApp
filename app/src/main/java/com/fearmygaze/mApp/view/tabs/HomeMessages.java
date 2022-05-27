package com.fearmygaze.mApp.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;

import java.util.ArrayList;
import java.util.List;

public class HomeMessages extends Fragment {
    View view;

    SwipeRefreshLayout refreshLayout;

    TextView conversationsNotFound;

    RecyclerView recyclerViewCon;

    AdapterConversation adapterConversation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_messages, container, false);

        refreshLayout = view.findViewById(R.id.homeMessagesRefreshLayout);
        conversationsNotFound = view.findViewById(R.id.homeMessagesConversationNotFound);
        recyclerViewCon = view.findViewById(R.id.homeMessagesConversation);

        List<Conversation> conversationList = new ArrayList<>();

        conversationList.add(new Conversation("1", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this a bool ??","22:20"));
        conversationList.add(new Conversation("2","https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Fl0m","is this a String ??","22:20"));
        conversationList.add(new Conversation("3","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));

        adapterConversation = new AdapterConversation(conversationList, requireActivity());

        if (adapterConversation.getItemCount() > 0){
            conversationsNotFound.setVisibility(View.GONE);
        }else{
            conversationsNotFound.setVisibility(View.VISIBLE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewCon.setLayoutManager(layoutManager);
        recyclerViewCon.setAdapter(adapterConversation);

        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));

        return view;
    }
}
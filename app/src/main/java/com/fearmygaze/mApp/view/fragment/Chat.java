package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment {

    View view;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView conversationsRecycler;

    AdapterConversation adapterConversation;

    List<Conversation> conversationList;

    /*
    * TODO: Find a way to cache the conversations(So the phone don't saturate the network
    * TODO: When the conversation adapter is clicked, pass the appropriate information and maybe store them locally (Preferable) so the new device doesn't have the customisations from the old one
    *
    * */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        swipeRefreshLayout = view.findViewById(R.id.adapterChatRefresh);
        conversationsRecycler = view.findViewById(R.id.fragmentChatAllConversationsRecycler);

        conversationList = new ArrayList<>();

        conversationList.add(new Conversation("1", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this a bool ??","22:22"));
        conversationList.add(new Conversation("2","https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Fl0m","is this a String ??","22:20"));
        conversationList.add(new Conversation("3","https://static-cdn.jtvnw.net/jtv_user_pictures/ae37597c-a887-4cb3-89f6-9fe7f3c16aa6-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("4","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("5","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("6","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("7","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("8","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("9","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("10","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("11","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));
        conversationList.add(new Conversation("12","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));


        adapterConversation = new AdapterConversation(conversationList, requireActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        conversationsRecycler.setLayoutManager(layoutManager);
        conversationsRecycler.setAdapter(adapterConversation);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        return view;
    }
}
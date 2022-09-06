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
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment {

    public Chat(User user) {
        this.user = user;
    }

    View view;
    User user;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView conversationsRecycler;

    AdapterConversation adapterConversation;

    List<Conversation> conversationList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        swipeRefreshLayout = view.findViewById(R.id.fragmentChatRefresh);
        conversationsRecycler = view.findViewById(R.id.fragmentChatAllConversationsRecycler);

        conversationList = new ArrayList<>();

        conversationList.add(new Conversation("1", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this a bool ??", "22:22"));
        conversationList.add(new Conversation("2", "https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Fl0m", "is this a String ??", "22:20"));
        conversationList.add(new Conversation("3", "https://static-cdn.jtvnw.net/jtv_user_pictures/d96af87b-949c-4074-ace1-48c1f94533b6-profile_image-70x70.png",
                "ThePrimeagen", "is this Rust ??", "22:20"));
        conversationList.add(new Conversation("4", "https://yt3.ggpht.com/NisKF9UIP0cD7okrboDbJhfPiP6Bp4Lw4I7YIP6y0GQkxgmU9Zb3kiS4zTClfkJeagoKzsuP=s48-c-k-c0x00ffffff-no-rj",
                "Hoonigan", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("5", "https://static-cdn.jtvnw.net/jtv_user_pictures/0bb9c502-ab5d-4440-9c9d-14e5260ebf86-profile_image-70x70.png",
                "ToggleBit", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("6", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("7", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("8", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("9", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("10", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("11", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));
        conversationList.add(new Conversation("12", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this an int ??", "22:20"));


        adapterConversation = new AdapterConversation(conversationList, requireActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        conversationsRecycler.setLayoutManager(layoutManager);
        conversationsRecycler.setAdapter(adapterConversation);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        return view;
    }
}
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
import com.fearmygaze.mApp.model.Friend;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;
import com.fearmygaze.mApp.view.adapter.AdapterFriendMini;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment {

    View view;

    SwipeRefreshLayout swipeRefreshLayout;

    MaterialTextView conversationsNotFound;

    RecyclerView friendsRecycler, conversationsRecycler;

    AdapterFriendMini adapterFriend;
    AdapterConversation adapterConversation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        swipeRefreshLayout = view.findViewById(R.id.adapterChatRefresh);
        friendsRecycler = view.findViewById(R.id.adapterChatFriendRecycler);
        conversationsNotFound = view.findViewById(R.id.adapterChatConversations);
        conversationsRecycler = view.findViewById(R.id.adapterChatConversationRecycler);


        List<Friend> friendList = new ArrayList<>();

        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Γιωργος Παπουλιας"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Ηλιας Αβρ"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Ταχμας"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem Ipsum"));

        adapterFriend = new AdapterFriendMini(friendList, requireActivity());


        List<Conversation> conversationList = new ArrayList<>();

        conversationList.add(new Conversation("1", "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this a bool ??","22:20"));
        conversationList.add(new Conversation("2","https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Fl0m","is this a String ??","22:20"));
        conversationList.add(new Conversation("3","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition","is this an int ??","22:20"));

        adapterConversation = new AdapterConversation(conversationList, requireActivity());


        RecyclerView.LayoutManager layoutManagerFriend = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        friendsRecycler.setLayoutManager(layoutManagerFriend);
        friendsRecycler.setAdapter(adapterFriend);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        conversationsRecycler.setLayoutManager(layoutManager);
        conversationsRecycler.setAdapter(adapterConversation);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        return view;
    }
}
package com.github.fearmygaze.mercury.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.interfaces.IConversationAdapter;
import com.github.fearmygaze.mercury.model.Conversation;
import com.github.fearmygaze.mercury.view.activity.ChatRoom;
import com.github.fearmygaze.mercury.view.adapter.AdapterConversation;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment implements IConversationAdapter {
    View view;

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

        conversationList.add(new Conversation(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "AnneMunition", "is this a bool ??", "22:22"));
        conversationList.add(new Conversation(2, "https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Fl0m", "is this a String ??", "22:20"));
        conversationList.add(new Conversation(3, "https://static-cdn.jtvnw.net/jtv_user_pictures/d96af87b-949c-4074-ace1-48c1f94533b6-profile_image-70x70.png",
                "ThePrimeagen", "is this Rust ??", "22:20"));
        conversationList.add(new Conversation(4, "https://yt3.ggpht.com/NisKF9UIP0cD7okrboDbJhfPiP6Bp4Lw4I7YIP6y0GQkxgmU9Zb3kiS4zTClfkJeagoKzsuP=s48-c-k-c0x00ffffff-no-rj",
                "Hoonigan", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(5, "https://static-cdn.jtvnw.net/jtv_user_pictures/0bb9c502-ab5d-4440-9c9d-14e5260ebf86-profile_image-70x70.png",
                "ToggleBit", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(6, "https://static-cdn.jtvnw.net/jtv_user_pictures/sequisha-profile_image-88c3a710aabaed1a-70x70.png",
                "Sequisha", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(7, "https://static-cdn.jtvnw.net/jtv_user_pictures/523565c2-3ec3-4973-b8c7-89407d3f6b2e-profile_image-70x70.png",
                "dev_spajus", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(8, "https://static-cdn.jtvnw.net/jtv_user_pictures/148c3b8a-a78b-4139-a429-7d3d90ce8a27-profile_image-70x70.png",
                "CohhCarnage", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(9, "https://static-cdn.jtvnw.net/jtv_user_pictures/05e1a82d-08b1-47fb-91b0-fa18ab388cf2-profile_image-70x70.png",
                "grimmmz", "is this an int ??", "22:20"));
        conversationList.add(new Conversation(10, "https://static-cdn.jtvnw.net/jtv_user_pictures/1bf618c5-037e-45a4-b77b-f203f39b4921-profile_image-70x70.png",
                "emree", "is this an int ??", "22:20"));

        adapterConversation = new AdapterConversation(conversationList, requireActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        conversationsRecycler.setLayoutManager(layoutManager);
        conversationsRecycler.setAdapter(adapterConversation);


        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        return view;
    }

    @Override
    public void onConversation(int pos) {//TODO: We need to remove the putExtra()
        Intent intent = new Intent(getContext(), ChatRoom.class);
        intent.putExtra("username", conversationList.get(pos).getUsername());
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDeleteConversation(int pos) {
        //TODO: We need to first finalize the way we create a room and then i will add the delete
    }

    @Override
    public void onRemoveFriend(int pos) {
        //TODO: We need to first finalize the way we create a room and then i will add the remove
    }

    @Override
    public void onReportUser(int pos) {
        //TODO: We need to first finalize the way we create a room and then i will add the report
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
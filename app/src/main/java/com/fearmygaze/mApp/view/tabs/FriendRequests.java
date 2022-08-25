package com.fearmygaze.mApp.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.FriendRequest;
import com.fearmygaze.mApp.view.adapter.AdapterFriendRequest;

import java.util.ArrayList;
import java.util.List;

public class FriendRequests extends Fragment {

    RecyclerView friendRequestRecycler;

    List<FriendRequest> friendRequestsList;
    AdapterFriendRequest adapterFriendRequest;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        friendRequestRecycler = view.findViewById(R.id.friendRequestRecycler);

        friendRequestsList = new ArrayList<>();

        friendRequestsList.add(new FriendRequest(1,"https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        friendRequestsList.add(new FriendRequest(1,"https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        friendRequestsList.add(new FriendRequest(1,"https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        friendRequestsList.add(new FriendRequest(1,"https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));



        adapterFriendRequest = new AdapterFriendRequest(friendRequestsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRequestRecycler.setLayoutManager(layoutManager);
        friendRequestRecycler.setAdapter(adapterFriendRequest);

        return view;
    }
}
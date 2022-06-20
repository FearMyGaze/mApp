package com.fearmygaze.mApp.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Friend;
import com.fearmygaze.mApp.view.adapter.AdapterFriendList;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends Fragment {

    RecyclerView friendRecycler;

    List<Friend> friendList;

    AdapterFriendList adapterFriendList;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        friendRecycler = view.findViewById(R.id.friendListRecycler);

        friendList = new ArrayList<>();

        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem_Ipsum"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem_Ipsum"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem_Ipsum"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem_Ipsum"));
        friendList.add(new Friend("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Lorem_Ipsum"));

        adapterFriendList = new AdapterFriendList(friendList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRecycler.setLayoutManager(layoutManager);
        friendRecycler.setAdapter(adapterFriendList);


        return view;
    }
}
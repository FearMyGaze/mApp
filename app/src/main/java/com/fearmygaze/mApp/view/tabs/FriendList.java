package com.fearmygaze.mApp.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.Controller.FriendController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IFriend;
import com.fearmygaze.mApp.model.Friend;
import com.fearmygaze.mApp.view.adapter.AdapterFriendList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendList extends Fragment {

    RecyclerView friendRecycler;

    List<Friend> friends;

    AdapterFriendList adapterFriendList;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        friendRecycler = view.findViewById(R.id.friendListRecycler);


        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRecycler.setLayoutManager(layoutManager);
        friendRecycler.setAdapter(adapterFriendList);

//        FriendController.showFriends(20, 0, requireContext(), new IFriend() {
//            @Override
//            public void onSuccess(List<Friend> friendList) {
//                friendListMain.addAll(friendList);
//            }
//
//            @Override
//            public void onError(String message) {
//                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });


        friendRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                fetchRows(lastVisibleItemPosition);

                if (dy <= 0) {
                    return;
                }

                fetchRows(lastVisibleItemPosition);

            }

            private void fetchRows(int lastVisibleItemPosition) {
                if (lastVisibleItemPosition >= adapterFriendList.getOffset()) {
                    adapterFriendList.setOffset(adapterFriendList.getOffset() + 10);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            FriendController.showFriends(20, adapterFriendList.getOffset(), getContext(), new IFriend() {
                                @Override
                                public void onSuccess(List<Friend> friendList) {
                                    adapterFriendList.addResultAndRefreshAdapter(friendList);
                                }

                                @Override
                                public void onError(String message) {

                                }
                            });
                        }
                    }, 666);
                }
            }
        });


//        friendList = new ArrayList<>();
//
//        friendList.add(new Friend(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png", "Lorem_Ipsum"));
//        friendList.add(new Friend(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png", "Lorem_Ipsum"));
//        friendList.add(new Friend(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png", "Lorem_Ipsum"));
//        friendList.add(new Friend(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png", "Lorem_Ipsum"));
//        friendList.add(new Friend(1, "https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png", "Lorem_Ipsum"));
//
//        adapterFriendList = new AdapterFriendList(friendList);

//        friendRecycler.setAdapter(adapterFriendList);


        return view;
    }
}
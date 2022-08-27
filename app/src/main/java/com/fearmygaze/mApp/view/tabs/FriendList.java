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
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.adapter.AdapterFriendList;

import java.util.List;

public class FriendList extends Fragment {

    public FriendList(User user){
        this.user = user;
    }

    RecyclerView friendRecycler;

    AdapterFriendList adapterFriendList;

    View view;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //TODO :We need to get the user data
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        System.out.println(user);
        friendRecycler = view.findViewById(R.id.friendListRecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRecycler.setLayoutManager(layoutManager);
        friendRecycler.setAdapter(adapterFriendList);

//        FriendController.showFriends(user.getID(), adapterFriendList.getOffset(), requireContext(), new IFriend() {
//            @Override
//            public void onSuccess(List<Friend> friendList) {
//                adapterFriendList.refillList(friendList);
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

                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (dy >= 0 && lastVisibleItemPosition >= adapterFriendList.getItemCount() - 1) {
                    fetchRows();
                }

            }

            private void fetchRows() {
                adapterFriendList.setOffset(adapterFriendList.getOffset() + 10);
                FriendController.showFriends(user.getId(), adapterFriendList.getOffset(), getContext(), new IFriend() {
                    @Override
                    public void onSuccess(List<Friend> friendList) {
                        adapterFriendList.addResultAndRefreshAdapter(friendList);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
            }
        });

        return view;
    }
}
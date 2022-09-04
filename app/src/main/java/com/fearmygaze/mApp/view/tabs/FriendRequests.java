package com.fearmygaze.mApp.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fearmygaze.mApp.Controller.FriendController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IFriendRequest;
import com.fearmygaze.mApp.model.FriendRequest;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.adapter.AdapterFriendRequest;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FriendRequests extends Fragment {

    public FriendRequests(User user){
        this.user = user;
    }

    User user;

    SwipeRefreshLayout refreshLayout;
    MaterialTextView friendRequestError;
    RecyclerView friendRequestRecycler;

    List<FriendRequest> friendRequests;
    AdapterFriendRequest adapterFriendRequest;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        refreshLayout = view.findViewById(R.id.friendRequestUpdate);
        friendRequestError = view.findViewById(R.id.friendRequestError);
        friendRequestRecycler = view.findViewById(R.id.friendRequestRecycler);

        friendRequests = new ArrayList<>();
        adapterFriendRequest = new AdapterFriendRequest(friendRequests, user);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRequestRecycler.setLayoutManager(layoutManager);
        friendRequestRecycler.setAdapter(adapterFriendRequest);

        FriendController.showRequests(user.getId(), adapterFriendRequest.getOffset(), view.getContext(), new IFriendRequest() {
            @Override
            public void onSuccess(List<FriendRequest> friendRequests) {
                friendRequestError.setVisibility(View.GONE);
                adapterFriendRequest.refillList(friendRequests);
            }

            @Override
            public void onError(String message) {
                adapterFriendRequest.clearListAndRefreshAdapter();
                friendRequestError.setVisibility(View.VISIBLE);
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        refreshLayout.setOnRefreshListener(this::refresh);

        friendRequestRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (dy >= 0 && lastVisibleItemPosition >= adapterFriendRequest.getItemCount() - 1) {
                    fetchRows();
                }
            }
            private void fetchRows() {
                adapterFriendRequest.setOffset(adapterFriendRequest.getOffset() + 10);
                FriendController.showRequests(user.getId(), adapterFriendRequest.getOffset(), getContext(), new IFriendRequest() {
                    @Override
                    public void onSuccess(List<FriendRequest> friendRequests) {
                        adapterFriendRequest.addResultAndRefreshAdapter(friendRequests);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
            }

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh(){
        FriendController.showRequests(user.getId(), adapterFriendRequest.getOffset(), view.getContext(), new IFriendRequest() {
            @Override
            public void onSuccess(List<FriendRequest> friendRequests) {
                adapterFriendRequest.refillList(friendRequests);
            }

            @Override
            public void onError(String message) {

            }
        });
        refreshLayout.setRefreshing(false);
    }

}
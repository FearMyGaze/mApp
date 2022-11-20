package com.github.fearmygaze.mercury.view.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.FriendController;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.interfaces.IFriendRequest;
import com.github.fearmygaze.mercury.interfaces.IFriendRequestAdapter;
import com.github.fearmygaze.mercury.interfaces.IVolley;
import com.github.fearmygaze.mercury.model.FriendRequest;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriendRequest;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FriendsRequests extends Fragment implements IFriendRequestAdapter {
    View view;

    PrivatePreference preference;
    AppDatabase database;
    User user;

    SwipeRefreshLayout refreshLayout;
    MaterialTextView friendRequestError;
    RecyclerView friendRequestRecycler;

    List<FriendRequest> friendRequests;
    AdapterFriendRequest adapterFriendRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends_requests, container, false);

        preference = new PrivatePreference(view.getContext());
        database = AppDatabase.getInstance(view.getContext());
        user = database.userDao().getUserByID(preference.getInt("id"));

        refreshLayout = view.findViewById(R.id.friendRequestUpdate);
        friendRequestError = view.findViewById(R.id.friendRequestError);
        friendRequestRecycler = view.findViewById(R.id.friendRequestRecycler);

        friendRequests = new ArrayList<>();
        adapterFriendRequest = new AdapterFriendRequest(friendRequests, user, this);

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

    private void refresh() {
        FriendController.showRequests(user.getId(), 0, view.getContext(), new IFriendRequest() {
            @Override
            public void onSuccess(List<FriendRequest> friendRequests) {
                friendRequestError.setVisibility(View.GONE);
                adapterFriendRequest.refillList(friendRequests);
            }

            @Override
            public void onError(String message) {
                friendRequestError.setVisibility(View.VISIBLE);
            }
        });
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClickAccept(int pos) {
        FriendController.answerFriendRequest(user.getId(), adapterFriendRequest.getFriendID(pos), "true", view.getContext(), new IVolley() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                adapterFriendRequest.removeItemAndRefresh(pos);

                if (adapterFriendRequest.getItemCount() == 0){
                    friendRequestError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO: Find a better way to handle the friend requests

    @Override
    public void onItemCLickIgnore(int pos) {
        FriendController.answerFriendRequest(user.getId(), adapterFriendRequest.getFriendID(pos), "false", view.getContext(), new IVolley() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                adapterFriendRequest.removeItemAndRefresh(pos);

                if (adapterFriendRequest.getItemCount() == 0){
                    friendRequestError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
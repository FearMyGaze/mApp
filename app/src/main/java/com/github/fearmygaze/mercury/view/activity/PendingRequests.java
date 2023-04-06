package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterFriendRequest;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class PendingRequests extends AppCompatActivity {

    Intent intent;
    String option, userID;

    SwipeRefreshLayout swipeRefreshLayout;
    ShapeableImageView goBack;
    TextView title, counter;
    ConstraintLayout parentError;

    RecyclerView recyclerView;
    AdapterFriendRequest adapterFriendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        swipeRefreshLayout = findViewById(R.id.pendingRequestsSwipe);
        goBack = findViewById(R.id.pendingRequestsGoBack);
        title = findViewById(R.id.pendingRequestsTitle);
        counter = findViewById(R.id.pendingRequestsCounter);
        parentError = findViewById(R.id.pendingRequestsError);
        recyclerView = findViewById(R.id.pendingRequestsRecycler);

        intent = getIntent();
        option = intent.getStringExtra("option");
        userID = intent.getStringExtra("id");

        goBack.setOnClickListener(v -> onBackPressed());

        if ("ignored".equals(option)) {
            adapterFriendRequest = new AdapterFriendRequest(new ArrayList<>(), userID, false);
        } else {
            adapterFriendRequest = new AdapterFriendRequest(new ArrayList<>(), userID, true);
        }
        showRequests();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            showRequests();
            swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(PendingRequests.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterFriendRequest);
    }

    private void showRequests() {
        if ("ignored".equals(option)) {
            Friends.ignoredList(userID, new Friends.OnExtendedListener() {
                @Override
                public void onResult(int resultCode, List<User> list) {
                    if (resultCode == 1 && list != null) {
                        title.setVisibility(View.VISIBLE);
                        counter.setVisibility(View.VISIBLE);
                        parentError.setVisibility(View.GONE);
                        title.setText(getString(R.string.pendingRequestsIgnored));
                        counter.setText(String.valueOf(list.size()));
                        adapterFriendRequest.setUsers(list);
                    } else {
                        title.setVisibility(View.GONE);
                        counter.setVisibility(View.GONE);
                        parentError.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(PendingRequests.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Friends.pendingList(userID, new Friends.OnExtendedListener() {
                @Override
                public void onResult(int resultCode, List<User> list) {
                    if (resultCode == 1 && list != null) {
                        title.setVisibility(View.VISIBLE);
                        counter.setVisibility(View.VISIBLE);
                        parentError.setVisibility(View.GONE);
                        title.setText(getString(R.string.pendingRequestsFriends));
                        counter.setText(String.valueOf(list.size()));
                        adapterFriendRequest.setUsers(list);
                    } else {
                        title.setVisibility(View.GONE);
                        counter.setVisibility(View.GONE);
                        parentError.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(PendingRequests.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
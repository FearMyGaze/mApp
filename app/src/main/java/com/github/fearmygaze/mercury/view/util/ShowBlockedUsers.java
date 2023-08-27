package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterBlocked;
import com.google.android.material.card.MaterialCardView;

public class ShowBlockedUsers extends AppCompatActivity {

    //Top Bar
    MaterialCardView goBack;
    SwipeRefreshLayout swipe;

    //Main Content
    RecyclerView recyclerView;
    AdapterBlocked adapterBlocked;
    FirestorePagingOptions<Request> options;

    //Intent
    Bundle bundle;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blocked_users);

        goBack = findViewById(R.id.showBlockedGoBack);
        swipe = findViewById(R.id.showBlockedSwipe);
        recyclerView = findViewById(R.id.showBlockedRecycler);

        bundle = getIntent().getExtras();

        if (bundle == null) onBackPressed();

        user = bundle.getParcelable("user");

        goBack.setOnClickListener(v -> onBackPressed());

        PagingConfig config = new PagingConfig(3, 15);
        options = new FirestorePagingOptions.Builder<Request>()
                .setLifecycleOwner(this)
                .setQuery(Friends.blockedQuery(user), config, Request.class)
                .build();

        adapterBlocked = new AdapterBlocked(user, options, recyclerView);
        recyclerView.setAdapter(adapterBlocked);
        recyclerView.setLayoutManager(new CustomLinearLayout(ShowBlockedUsers.this, LinearLayoutManager.VERTICAL, false));

        adapterBlocked.addLoadStateListener(combinedLoadStates -> {
            LoadState append = combinedLoadStates.getAppend();
            if (append instanceof LoadState.NotLoading) {
                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                if (notLoading.getEndOfPaginationReached()) {
                    if (adapterBlocked.getItemCount() == 0) {
                        Toast.makeText(ShowBlockedUsers.this, "Bravo re", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return null;
        });

        swipe.setOnRefreshListener(() -> {
            adapterBlocked.updateOptions(options);
            swipe.setRefreshing(false);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterBlocked.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterBlocked.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

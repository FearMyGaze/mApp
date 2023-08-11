package com.github.fearmygaze.mercury.view.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterRequest;

public class FragmentWaitingRequest extends Fragment {

    public FragmentWaitingRequest() {
    }

    View view;
    String id;

    AdapterRequest adapterRequest;
    FirestorePagingOptions<Request> options;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(User.ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);
        SwipeRefreshLayout swipe = view.findViewById(R.id.fragmentRequestsSwipe);
        RecyclerView recyclerView = view.findViewById(R.id.fragmentRequestsRecycler);
        ConstraintLayout errorLayout = view.findViewById(R.id.fragmentRequestsErrorLayout);

        User user = AppDatabase.getInstance(view.getContext()).userDao().getUserByUserID(id);
        PagingConfig config = new PagingConfig(20, 10);
        options = new FirestorePagingOptions.Builder<Request>()
                .setLifecycleOwner(this)
                .setQuery(Friends.waitingQuery(user), config, Request.class)
                .build();
        adapterRequest = new AdapterRequest(user, options, recyclerView, requireActivity());
        recyclerView.setAdapter(adapterRequest);
        recyclerView.setLayoutManager(new CustomLinearLayout(requireActivity(), LinearLayoutManager.VERTICAL, false));

        swipe.setOnRefreshListener(() -> {
            adapterRequest.updateOptions(new FirestorePagingOptions.Builder<Request>()
                    .setLifecycleOwner(this)
                    .setQuery(Friends.waitingQuery(user), config, Request.class)
                    .build());
            swipe.setRefreshing(false);
        });
        return view;
    }

    public static FragmentWaitingRequest newInstance(String id) {
        FragmentWaitingRequest fragment = new FragmentWaitingRequest();
        Bundle bundle = new Bundle();
        bundle.putString(User.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRequest.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRequest.stopListening();
    }
}

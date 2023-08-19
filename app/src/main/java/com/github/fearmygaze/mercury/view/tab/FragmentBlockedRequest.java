package com.github.fearmygaze.mercury.view.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterRequest;

public class FragmentBlockedRequest extends Fragment {

    public FragmentBlockedRequest() {
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
        view = inflater.inflate(R.layout.fragment_blocked, container, false);
        SwipeRefreshLayout swipe = view.findViewById(R.id.fragmentBlockedSwipe);
        RecyclerView recyclerView = view.findViewById(R.id.fragmentBlockedRecycler);
        ConstraintLayout errorLayout = view.findViewById(R.id.fragmentBlockedErrorLayout);

        User user = AppDatabase.getInstance(view.getContext()).userDao().getByID(id);
//        PagingConfig config = new PagingConfig(3, 15);
//        options = new FirestorePagingOptions.Builder<Request1>()
//                .setLifecycleOwner(this)
//                .setQuery(Friends.blockedQuery(user), config, Request1.class)
//                .build();

//        adapterRequest = new AdapterRequest(user, AdapterRequest.OPTION_BLOCK, options, recyclerView, requireActivity());
//        recyclerView.setAdapter(adapterRequest);
//        recyclerView.setLayoutManager(new CustomLinearLayout(requireActivity(), LinearLayoutManager.VERTICAL, false));

//        swipe.setOnRefreshListener(() -> {
//            adapterRequest.updateOptions(new FirestorePagingOptions.Builder<Request1>()
//                    .setLifecycleOwner(this)
//                    .setQuery(Friends.blockedQuery(user), config, Request1.class)
//                    .build());
//            swipe.setRefreshing(false);
//        });
        return view;
    }

    public static FragmentBlockedRequest newInstance(String id) {
        FragmentBlockedRequest fragment = new FragmentBlockedRequest();
        Bundle bundle = new Bundle();
        bundle.putString(User.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

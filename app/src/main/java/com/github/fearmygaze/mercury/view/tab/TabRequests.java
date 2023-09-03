package com.github.fearmygaze.mercury.view.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.model.Request;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterPending;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TabRequests extends Fragment {

    public TabRequests() {
    }

    View view;
    String id;

    AdapterPending adapterPending;
    RecyclerView recyclerView;
    ConstraintLayout errorLayout;

    public static TabRequests newInstance(String id) {
        TabRequests fragment = new TabRequests();
        Bundle bundle = new Bundle();
        bundle.putString(User.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }


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
        recyclerView = view.findViewById(R.id.fragmentRequestsRecycler);
        errorLayout = view.findViewById(R.id.fragmentRequestsErrorLayout);

        User user = AppDatabase.getInstance(view.getContext()).userDao().getByID(id);

        fetch(user);

        adapterPending = new AdapterPending(user, new ArrayList<>());
        recyclerView.setAdapter(adapterPending);
        recyclerView.setLayoutManager(new CustomLinearLayout(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(null);

        swipe.setOnRefreshListener(() -> {
            fetch(user);
            swipe.setRefreshing(false);
        });
        return view;
    }

    public void fetch(User user) {
        Friends.waitingQuery(user)
                .limit(50)
                .get()
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Request> list = new ArrayList<>();
                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            list.add(snapshot.toObject(Request.class));
                        }
                        errorLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapterPending.set(list);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

}

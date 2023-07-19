package com.github.fearmygaze.mercury.view.tab;

import android.content.Context;
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
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Friends;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUsersResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterUser;

import java.util.ArrayList;
import java.util.List;

public class FragmentBlockedRequest extends Fragment {

    public FragmentBlockedRequest() {
    }

    View view;
    String id;

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

        User user = AppDatabase.getInstance(view.getContext()).userDao().getUserByUserID(id);
        AdapterUser adapterUser = new AdapterUser(new ArrayList<>(), user.getId(), AdapterUser.TYPE_BLOCKED);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterUser);

        getList(user, adapterUser, view.getContext(), swipe, errorLayout);

        swipe.setOnRefreshListener(() -> getList(user, adapterUser, view.getContext(), swipe, errorLayout));
        return view;
    }

    public static FragmentBlockedRequest newInstance(String id) {
        FragmentBlockedRequest fragment = new FragmentBlockedRequest();
        Bundle bundle = new Bundle();
        bundle.putString(User.ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void getList(User user, AdapterUser adapterUser, Context context, SwipeRefreshLayout swipe, ConstraintLayout errorLayout) {
        Friends.getRequestedList(user, Friends.LIST_BLOCKED, context, new OnUsersResponseListener() {
            @Override
            public void onSuccess(int code, List<User> list) {
                if (code == 0) {
                    errorLayout.setVisibility(View.GONE);
                    adapterUser.setData(list);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

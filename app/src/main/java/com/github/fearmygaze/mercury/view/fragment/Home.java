package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.RoomActions;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterRoom;

public class Home extends Fragment {

    View view;
    User user;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    FirestoreRecyclerOptions<Room> options;
    AdapterRoom adapterRoom;

    public Home() {

    }

    public static Home newInstance(User user) {
        Home home = new Home();
        Bundle bundle = new Bundle();
        bundle.putParcelable(User.PARCEL, user);
        home.setArguments(bundle);
        return home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(User.PARCEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        refreshLayout = view.findViewById(R.id.homeRefresh);
        recyclerView = view.findViewById(R.id.homeRecycler);

        options = new FirestoreRecyclerOptions.Builder<Room>()
                .setQuery(new RoomActions(getContext()).getRooms(user.getId()), Room.class)
                .setLifecycleOwner(this)
                .build();

        adapterRoom = new AdapterRoom(user, options, recyclerView);
        recyclerView.setLayoutManager(new CustomLinearLayout(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRoom);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    Log.d("customLog", String.valueOf(layoutManager.findFirstCompletelyVisibleItemPosition() == 0));
                    refreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
                }
            }
        });

        refreshLayout.setOnRefreshListener(() -> {
            adapterRoom.updateOptions(options);
            refreshLayout.setRefreshing(false);
        });

        return view;
    }

}

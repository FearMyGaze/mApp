package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.SearchedUser;
import com.fearmygaze.mApp.view.adapter.AdapterSearch;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    View view;

    SearchView searchView;

    TextView usersNotFound;

    RecyclerView recyclerView;

    AdapterSearch adapterSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchSearchView);
        usersNotFound = view.findViewById(R.id.searchUsersNotFound);
        recyclerView = view.findViewById(R.id.searchRecycler);

        searchView.clearFocus(); //in some lower api devices the searchView gets instantly focus


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });


        List<SearchedUser> searchedUserList = new ArrayList<>();
        searchedUserList.add(new SearchedUser("1", "https://static-cdn.jtvnw.net/jtv_user_pictures/a7f1c81b-6f00-41fc-8da9-18aabf169e75-profile_image-70x70.png",
                "Lorem Ipsum","@LoremIpsum"));
        searchedUserList.add(new SearchedUser("2", "https://static-cdn.jtvnw.net/jtv_user_pictures/a7f1c81b-6f00-41fc-8da9-18aabf169e75-profile_image-70x70.png",
                "Lorem Ipsum","@LoremIpsum #5467"));
        searchedUserList.add(new SearchedUser("3", "https://static-cdn.jtvnw.net/jtv_user_pictures/a7f1c81b-6f00-41fc-8da9-18aabf169e75-profile_image-70x70.png",
                "Lorem Ipsum","#2345"));

        adapterSearch = new AdapterSearch(searchedUserList);

        if (adapterSearch.getItemCount() > 0){
            usersNotFound.setVisibility(View.GONE);
        }else{
            usersNotFound.setVisibility(View.VISIBLE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterSearch);



        return view;
    }
    private void showToast(String message, int duration){
        Toast.makeText(requireContext(), message, duration).show();
    }
}
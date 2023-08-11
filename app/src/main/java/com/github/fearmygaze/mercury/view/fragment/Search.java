package com.github.fearmygaze.mercury.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.activity.Main;
import com.github.fearmygaze.mercury.view.adapter.AdapterSearch;

public class Search extends Fragment {

    View view;
    User user;

    EditText search;

    RecyclerView recyclerView;

    AdapterSearch adapterSearch;
    FirestorePagingOptions<User> searchOptions;

    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    public Search() {
    }

    public static Search newInstance(User user) {
        Search search = new Search();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        search.setArguments(bundle);
        return search;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.searchRecycler);
        search = ((Main) requireActivity()).getSearchBar();
        searchOptions = ((Main) requireActivity()).getEmptyOption();

        adapterSearch = new AdapterSearch(user, searchOptions, recyclerView, requireActivity());
        recyclerView.setLayoutManager(new CustomLinearLayout(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() >= 3) {
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> adapterSearch.updateOptions(new FirestorePagingOptions.Builder<User>()
                            .setLifecycleOwner(searchOptions.getOwner())
                            .setQuery(Auth.searchQuery(editable.toString().trim()),
                                    new PagingConfig(3, 15), User.class)
                            .build());
                    searchHandler.postDelayed(searchRunnable, 350);
                } else {
                    searchHandler.removeCallbacks(searchRunnable);
                    adapterSearch.updateOptions(searchOptions);
                }
            }
        });

        adapterSearch.addLoadStateListener(combinedLoadStates -> {
            LoadState append = combinedLoadStates.getAppend();
            if (append instanceof LoadState.NotLoading) {
                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                if (notLoading.getEndOfPaginationReached()) {
                    if (adapterSearch.getItemCount() == 0) {
//                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
//                        errorLayout.setVisibility(View.GONE);
                    }
                }
            }
            return null;
        });

        return view;
    }

}

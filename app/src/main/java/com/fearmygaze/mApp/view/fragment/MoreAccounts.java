package com.fearmygaze.mApp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.view.adapter.AdapterMoreAccounts;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MoreAccounts extends BottomSheetDialogFragment {
    
    View view;
    
    RecyclerView recyclerView;
    
    MaterialButton addExisting, createNew;
    
    List<User> users;

    AdapterMoreAccounts accounts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_more_accounts, container, false);

        recyclerView = view.findViewById(R.id.moreAccountsRecycler);
        addExisting = view.findViewById(R.id.moreAccountsAddExisting);
        createNew = view.findViewById(R.id.moreAccountsCreateNew);

        users = new ArrayList<>();

        users.add(new User(1, "asd0", "asd0", "asd0@email.com"));
        users.add(new User(20, "asd1", "asd1", "asd1@email.com"));
        users.add(new User(22, "asd1", "asd1", "asd1@email.com"));
        users.add(new User(22, "asd1", "asd1", "asd1@email.com"));
        users.add(new User(22, "asd1", "asd1", "asd1@email.com"));

        accounts = new AdapterMoreAccounts(users, 20, pos -> {
            Toast.makeText(view.getContext(),accounts.getUser(pos).getEmail() , Toast.LENGTH_LONG).show();
            dismiss();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(accounts);

        return view;
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}
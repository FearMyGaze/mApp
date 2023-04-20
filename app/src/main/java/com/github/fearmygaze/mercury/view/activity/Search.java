package com.github.fearmygaze.mercury.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.adapter.AdapterUserList;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Search extends AppCompatActivity {

    ShapeableImageView goBack;
    TextInputEditText search;

    AdapterUserList adapterUserList;
    List<User> searchedUsers;
    RecyclerView searchRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        goBack = findViewById(R.id.searchGoBack);
        search = findViewById(R.id.searchContainer);
        searchRecycler = findViewById(R.id.searchRecycler);
        searchedUsers = new ArrayList<>();

        goBack.setOnClickListener(v -> onBackPressed());

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {//TODO: Bring a set amount of users and update them on scroll
                if (s.toString().trim().length() >= 3) { //TODO: Make the search for location, job
                    if (s.toString().trim().startsWith("@")) {
                        searchedUsers.clear();
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .orderByChild("username").startAt(s.toString().trim()).endAt(s.toString().trim() + "\uf8ff")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot user : snapshot.getChildren()) {
                                                searchedUsers.add(new User(
                                                        Objects.requireNonNull(user.child("userUID").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("username").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("name").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("imageURL").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("showFriends").getValue(Boolean.class))
                                                ));
                                            }
                                            adapterUserList.setUsers(searchedUsers);
                                        } else adapterUserList.clearUsers();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Search.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        searchedUsers.clear();
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .orderByChild("name").startAt(s.toString().trim()).endAt(s.toString().trim() + "\uf8ff")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot user : snapshot.getChildren()) {
                                                searchedUsers.add(new User(
                                                        Objects.requireNonNull(user.child("userUID").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("username").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("name").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("imageURL").getValue(String.class)),
                                                        Objects.requireNonNull(user.child("showFriends").getValue(Boolean.class))
                                                ));
                                            }
                                            adapterUserList.setUsers(searchedUsers);
                                        } else adapterUserList.clearUsers();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Search.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else adapterUserList.clearUsers();
            }
        });

        adapterUserList = new AdapterUserList(new ArrayList<>(), FirebaseAuth.getInstance().getUid(), false);
        searchRecycler.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
        searchRecycler.setAdapter(adapterUserList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
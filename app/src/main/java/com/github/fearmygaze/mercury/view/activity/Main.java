package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.Communications;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUsersResponseListener;
import com.github.fearmygaze.mercury.model.Room;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterRequests;
import com.github.fearmygaze.mercury.view.adapter.AdapterRoom;
import com.github.fearmygaze.mercury.view.adapter.AdapterUser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    //App Card
    MaterialCardView settingsBtn, notificationsBtn, requestsBtn, profileBtn;
    ShapeableImageView profileImage;

    //Actions
    ExtendedFloatingActionButton actions;
    FloatingActionButton roomFab, searchFab;
    Group actionGroup;

    //User
    User user;

    //ChatRooms
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    AdapterRoom adapterRoom;

    //BottomSheets
    BottomSheetBehavior<ConstraintLayout> notificationSheetBehavior;
    BottomSheetBehavior<ConstraintLayout> requestsSheetBehavior;
    BottomSheetBehavior<ConstraintLayout> searchSheetBehavior;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //General
        refreshLayout = findViewById(R.id.mainSwipeRefresh);

        //App Card
        settingsBtn = findViewById(R.id.mainSettingsButton);
        notificationsBtn = findViewById(R.id.mainNotificationsButton);
        requestsBtn = findViewById(R.id.mainRequestsButton);
        profileBtn = findViewById(R.id.mainProfileButton);
        profileImage = findViewById(R.id.mainProfileImage);

        //Actions
        actions = findViewById(R.id.mainExtendedFab);
        actions.shrink();
        roomFab = findViewById(R.id.mainRoomFab);
        searchFab = findViewById(R.id.mainSearchFab);
        actionGroup = findViewById(R.id.mainGroup);

        //ChatRooms
        recyclerView = findViewById(R.id.mainRecycler);

        //User
        rememberMe();

        adapterRoom = new AdapterRoom(user,
                new FirestoreRecyclerOptions.Builder<Room>().setQuery(Communications.getRooms(user), Room.class).build(),
                recyclerView, this);
        recyclerView.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRoom);

        actions.setOnClickListener(v -> fabController());

        settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class).putExtra(User.ID, user.getId()));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        notificationsBtn.setOnClickListener(v -> notificationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        requestsBtn.setOnClickListener(v -> requestsSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        profileBtn.setOnClickListener(v -> Tools.goToProfile(user.getId(), this, this));

        profileBtn.setOnLongClickListener(v -> {
            //Account Switching here
            return true;
        });

        searchFab.setOnClickListener(v -> {
            fabController();
            searchSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        roomFab.setOnClickListener(v -> {
            fabController();
            startActivity(new Intent(Main.this, RoomCreator.class)
                    .putExtra(User.ID, user.getId()));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));
    }

    private void fabController() {
        if (actionGroup.getVisibility() == View.VISIBLE) {
            actionGroup.setVisibility(View.GONE);
            actions.shrink();
        } else {
            actions.extend();
            actionGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterRoom.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRoom.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rememberMe();
    }

    @Override
    public void onBackPressed() {
        if (requestsSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            requestsSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        if (searchSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            Tools.closeKeyboard(Main.this);
            searchSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        if (notificationSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            notificationSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        super.onBackPressed();
    }

    private void requestsSheet(String id) {
        ConstraintLayout bottomSheetParent = findViewById(R.id.requestsSheetParent);
        requestsSheetBehavior = BottomSheetBehavior.from(bottomSheetParent);

        AdapterRequests adapter = new AdapterRequests(Main.this, id);

        ShapeableImageView goBack = findViewById(R.id.requestsSheetGoBack);
        TabLayout tabLayout = findViewById(R.id.requestsSheetTabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.requestsSheetViewPager);

        goBack.setOnClickListener(v -> onBackPressed());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 1) {
                tab.setText(getString(R.string.requestBlocked));
            } else tab.setText(getString(R.string.requestWaiting));
        }).attach();

    }

    private void searchSheet(String id) {
        ConstraintLayout bottomSheetParent = findViewById(R.id.searchSheetParent);
        searchSheetBehavior = BottomSheetBehavior.from(bottomSheetParent);

        ShapeableImageView goBack = findViewById(R.id.searchGoBack);
        TextInputEditText searchBox = findViewById(R.id.searchContainer);
        ConstraintLayout errorLayout = findViewById(R.id.searchErrorLayout);
        RecyclerView recyclerView = findViewById(R.id.searchRecycler);
        AdapterUser adapterUser = new AdapterUser(users, id, AdapterUser.TYPE_SEARCH);
        recyclerView.setLayoutManager(new LinearLayoutManager(Main.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterUser);

        goBack.setOnClickListener(v -> onBackPressed());
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() >= 3) {
                    Auth.searchQuery(editable.toString(), Main.this, new OnUsersResponseListener() {
                        @Override
                        public void onSuccess(int code, List<User> list) {
                            if (code == 0 && !list.isEmpty()) {
                                errorLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapterUser.clearData();
                                users.addAll(list);
                                adapterUser.setData(users);
                            } else {
                                errorLayout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void notificationSheet(String id) {
        ConstraintLayout bottomSheetParent = findViewById(R.id.notificationsSheetParent);
        notificationSheetBehavior = BottomSheetBehavior.from(bottomSheetParent);

        ShapeableImageView goBack = findViewById(R.id.notificationsSheetGoBack);
        ConstraintLayout errorLayout = findViewById(R.id.notificationsSheetErrorLayout);
        TextView errorMsg = findViewById(R.id.notificationsSheetErrorTitle);
        RecyclerView recyclerView = findViewById(R.id.notificationsSheetRecycler);


        goBack.setOnClickListener(v -> onBackPressed());

    }

    private void rememberMe() {
        FirebaseUser oldUserID = FirebaseAuth.getInstance().getCurrentUser();
        if (oldUserID != null) {
            User oldUser = AppDatabase.getInstance(Main.this).userDao().getUserByUserID(oldUserID.getUid());
            if (oldUser != null) {
                user = oldUser;
                Tools.profileImage(oldUser.getImage(), Main.this).into(profileImage);
                requestsSheet(user.getId());
                searchSheet(user.getId());
                notificationSheet(user.getId());
            }
        } else FirebaseAuth.getInstance().signOut();

        Auth.rememberMe(Main.this, new OnUserResponseListener() {
            @Override
            public void onSuccess(int code, User data) {
                switch (code) {
                    case 0:
                        user = data;
                        Tools.profileImage(user.getImage(), Main.this).into(profileImage);
                        break;
                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Main.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main.this, SignIn.class));
                        finish();
                        break;
                    case 2:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Main.this, "You need to activate your account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main.this, SignIn.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Main.this, SignIn.class));
                finish();
            }
        });
    }
}

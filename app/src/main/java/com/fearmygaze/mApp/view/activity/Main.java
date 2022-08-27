package com.fearmygaze.mApp.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fearmygaze.mApp.Controller.FriendController;
import com.fearmygaze.mApp.Controller.IssueController;
import com.fearmygaze.mApp.Controller.UserController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.ISearch;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.model.SearchedUser;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.util.PrivatePreference;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.adapter.AdapterSearch;
import com.fearmygaze.mApp.view.fragment.Chat;
import com.fearmygaze.mApp.view.fragment.Friends;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    public Fragment friends, chat;

    DrawerLayout drawerLayout;

    MaterialToolbar toolbar;

    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;

    View header;

    BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    ConstraintLayout bottomSheetConstraint;
    AdapterSearch adapterSearch;

    RecyclerView searchRecycler;

    TextView usersNotFound;
    SearchView searchView;

    boolean notifications = true;

    PrivatePreference preference;
    User currentUser;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        preference = new PrivatePreference(Main.this);

        if (getIntent().getParcelableExtra("user") != null) {
            currentUser = getIntent().getParcelableExtra("user");
        } else {
            currentUser = new User(
                    preference.getInt("id"), preference.getString("username"),
                    preference.getString("image"), preference.getString("email"));
        }

        rememberMe();

        drawerLayout = findViewById(R.id.mainDrawer);
        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigation);
        navigationView = findViewById(R.id.mainNavigation);
        bottomSheetConstraint = findViewById(R.id.search);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigationMenuItemProfile:
                    startActivity(new Intent(Main.this, Profile.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    drawerLayout.close();
                    return true;
                case R.id.navigationMenuItemNotifications:
                    startActivity(new Intent(Main.this, Notifications.class));
                    return true;
                case R.id.navigationMenuItemSettings:
                    startActivity(new Intent(Main.this, Settings.class));
                    return true;
                case R.id.navigationMenuItemBug:
                    prepareForBugListing();
                    return true;
                case R.id.navigationMenuItemFeature:
                    prepareForFeatureListing();
                    return true;
                case R.id.navigationMenuItemChangelog:
                    Toast.makeText(Main.this, "This will open a dialog with MarkDown Support", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigationMenuItemTerms:
                    Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigationMenuItemSignOut:
                    preference.clear();
                    finish();
                    Toast.makeText(this, "User Signed out", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        });

        initializeToolbar(toolbar);
        initializeBottomSearch();

        /*
         * BottomNavigation
         * */

        friends = new Friends(currentUser);
        chat = new Chat(currentUser);

        replaceFragment(chat);
        bottomNavigationView.setSelectedItemId(R.id.mainNavigationItemChoice1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainNavigationItemChoice1:
                    replaceFragment(chat);
                    return true;
                case R.id.mainNavigationItemChoice2:
                    replaceFragment(friends);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void rememberMe() {//TODO: Maybe we need to bring the data back if there are new changes
        preference = new PrivatePreference(Main.this);
        if (preference.getInt("id") == -1 || currentUser.getId() == -1) {
            preference.clear();
            startActivity(new Intent(Main.this, Starting.class));
            finish();
            return;
        }
        UserController.statusCheck(currentUser.getId(), Main.this, new IVolley() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(Main.this, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(Main.this, "message", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Main.this, Starting.class));
                finish();
            }
        });
    }


    private void prepareForBugListing() {//TODO: Add a textView so the user can input device name
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bug);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputLayout dialogBugDescError = dialog.findViewById(R.id.dialogBugDescError);
        TextInputEditText dialogBugDesc = dialog.findViewById(R.id.dialogBugDesc);

        TextInputLayout dialogBugDeviceError = dialog.findViewById(R.id.dialogBugDeviceError);
        TextInputEditText dialogBugDevice = dialog.findViewById(R.id.dialogBugDevice);

        MaterialCheckBox checkBox = dialog.findViewById(R.id.dialogBugFurtherCommunication);

        MaterialButton cancel = dialog.findViewById(R.id.dialogBugCancel);
        MaterialButton confirm = dialog.findViewById(R.id.dialogBugConfirm);


        dialogBugDesc.addTextChangedListener(new TextHandler(dialogBugDescError));

        cancel.setOnClickListener(v -> dialog.cancel());

        confirm.setOnClickListener(v -> {
            if (!dialogBugDescError.isErrorEnabled()) {
                if (TextHandler.isTextInputLengthCorrect(dialogBugDesc, dialogBugDescError, 300, v.getContext()) &&
                        TextHandler.isTextInputLengthCorrect(dialogBugDevice, dialogBugDeviceError, 100, v.getContext())) {

                    String desc = Objects.requireNonNull(dialogBugDesc.getText()).toString().trim();
                    String device = Objects.requireNonNull(dialogBugDevice.getText()).toString().trim();

                    IssueController.uploadBug(currentUser.getId(), desc, device, checkBox.isChecked(), getApplicationContext(), new IVolley() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            drawerLayout.close();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        dialog.show();

    }

    private void prepareForFeatureListing() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_feature);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputLayout dialogBugDescError = dialog.findViewById(R.id.dialogFeatureDescError);
        TextInputEditText dialogBugDesc = dialog.findViewById(R.id.dialogFeatureDesc);

        MaterialCheckBox checkBox = dialog.findViewById(R.id.dialogFeatureFurtherCommunication);

        MaterialButton cancel = dialog.findViewById(R.id.dialogFeatureCancel);
        MaterialButton confirm = dialog.findViewById(R.id.dialogFeatureConfirm);

        dialogBugDesc.addTextChangedListener(new TextHandler(dialogBugDescError));

        cancel.setOnClickListener(v -> dialog.cancel());

        confirm.setOnClickListener(v -> {
            if (!dialogBugDescError.isErrorEnabled()) {
                if (TextHandler.isTextInputLengthCorrect(dialogBugDesc, dialogBugDescError, 300, v.getContext())) {
                    String desc = Objects.requireNonNull(dialogBugDesc.getText()).toString().trim();
                    IssueController.uploadFeature(currentUser.getId(), desc, checkBox.isChecked(), getApplicationContext(), new IVolley() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            drawerLayout.close();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        dialog.show();

    }

    private void initializeToolbar(MaterialToolbar toolbar) { //TODO: We need to optimize the glide stuff and the order of operations
        /*This add the icon of the user*/
        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .apply(new RequestOptions().override(70, 70))
                .load(currentUser.getImageUrl())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        toolbar.setNavigationIcon(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        /* This changes the icon when you click it */
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainToolbarItemNotifications) {
                if (notifications) {
                    notifications = false;
                    item.setIcon(R.drawable.ic_notifications_off_24);
                } else {
                    notifications = true;
                    item.setIcon(R.drawable.ic_notifications_active_24);
                }
            }
            return true;
        });

    }

    private void initializeBottomSearch() {
        searchRecycler = bottomSheetConstraint.findViewById(R.id.searchRecycler);
        usersNotFound = bottomSheetConstraint.findViewById(R.id.searchUsersNotFound);

        List<SearchedUser> searchedUserList = new ArrayList<>();
        adapterSearch = new AdapterSearch(searchedUserList);

        sheetBehavior = BottomSheetBehavior.from(bottomSheetConstraint);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setExpandedOffset(1);
        sheetBehavior.setSkipCollapsed(true);
        sheetBehavior.setDraggable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Main.this, LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(layoutManager);
        searchRecycler.setAdapter(adapterSearch);


        searchRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (dy >= 0 && lastVisibleItemPosition >= adapterSearch.getItemCount() - 1) {
                    fetchRows();
                }

            }

            private void fetchRows() {
                adapterSearch.setOffset(adapterSearch.getOffset() + 10);
                FriendController.searchUser(currentUser, searchView.getQuery().toString().trim(), adapterSearch.getOffset(), searchView.getContext(), new ISearch() {
                    @Override
                    public void onSuccess(List<SearchedUser> searchedUserList) {
                        usersNotFound.setVisibility(View.GONE);
                        adapterSearch.addResultAndRefreshAdapter(searchedUserList);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.mainToolbarItemSearch);
        MenuItem menuItem1 = menu.findItem(R.id.mainToolbarItemNotifications);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //TODO: IF query == new query do nothing
                adapterSearch.setOffset(0);
                if (!query.isEmpty()) {
                    FriendController.searchUser(currentUser, query.trim(), adapterSearch.getOffset(), searchView.getContext(), new ISearch() {
                        @Override
                        public void onSuccess(List<SearchedUser> searchedUserList) {
                            usersNotFound.setVisibility(View.GONE);
                            adapterSearch.refillList(searchedUserList);
                        }

                        @Override
                        public void onError(String message) {
                            adapterSearch.clearListAndRefreshAdapter();
                            usersNotFound.setVisibility(View.VISIBLE);
                            Toast.makeText(Main.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menuItem1.setVisible(false);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menuItem1.setVisible(true);
                adapterSearch.clearListAndRefreshAdapter();
                usersNotFound.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {//TODO: This is not working
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit();
    }

}
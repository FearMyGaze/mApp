package com.github.fearmygaze.mercury.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.fearmygaze.mercury.BuildConfig;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.FriendController;
import com.github.fearmygaze.mercury.controller.IssueController;
import com.github.fearmygaze.mercury.controller.UserController;
import com.github.fearmygaze.mercury.custom.EventNotifier;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.database.UserDao;
import com.github.fearmygaze.mercury.interfaces.ISearch;
import com.github.fearmygaze.mercury.interfaces.IUserStatus;
import com.github.fearmygaze.mercury.interfaces.IVolley;
import com.github.fearmygaze.mercury.model.SearchedUser;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.PrivatePreference;
import com.github.fearmygaze.mercury.util.TextHandler;
import com.github.fearmygaze.mercury.view.adapter.AdapterSearch;
import com.github.fearmygaze.mercury.view.fragment.MoreAccounts;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    DrawerLayout drawerLayout;

    ConstraintLayout mainRoot;

    MaterialToolbar toolbar;

    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;
    View navigationHeader;
    TextView navigationFooterTerms, navigationFooterChangelog;

    BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    ConstraintLayout bottomSheetConstraint;
    AdapterSearch adapterSearch;
    RecyclerView searchRecycler;
    TextView searchedUserNotFound;
    SearchView searchView;

    //AppBarConfiguration appBarConfiguration;
    NavController navController;

    boolean notifications = true;

    PrivatePreference preference;
    AppDatabase database;
    UserDao userDao;
    User user;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(Main.this);
        drawerLayout = findViewById(R.id.mainDrawer);
        mainRoot = findViewById(R.id.mainRoot);
        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigation);
        navigationView = findViewById(R.id.mainNavigation);
        navigationHeader = navigationView.getHeaderView(0);
        navigationFooterTerms = findViewById(R.id.navFooterTerms);
        navigationFooterChangelog = findViewById(R.id.navFooterChangelog);
        bottomSheetConstraint = findViewById(R.id.search);

        setSupportActionBar(toolbar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        preference = new PrivatePreference(Main.this);
        userDao = database.userDao();

        if (preference.getInt("id") != -1) {
            user = userDao.getUserByID(preference.getInt("id"));
        } else {
            preference.clear();
            startActivity(new Intent(Main.this, Starting.class));
            finish();
        }

        rememberMe();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        setUserComponents();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigationMenuItemProfile:
                    drawerLayout.close();
                    startActivity(new Intent(Main.this, Profile.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                case R.id.navigationMenuItemSettings:
                    drawerLayout.close();
                    startActivity(new Intent(Main.this, Settings.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                case R.id.navigationMenuItemBug:
                    prepareForBugListing();
                    return true;
                case R.id.navigationMenuItemFeature:
                    prepareForFeatureListing();
                    return true;
                case R.id.navigationMenuItemSignOut:
                    userDao.deleteUserByID(preference.getInt("id"));
                    preference.clear();
                    startActivity(new Intent(this, Starting.class));
                    finish();
                    Toast.makeText(this, "User Signed out", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        });

        navigationFooterTerms.setOnClickListener(v -> {
            EventNotifier.event(v, "This will open a dialog", EventNotifier.LENGTH_SHORT);
        });

        navigationFooterChangelog.setOnClickListener(v -> {
            EventNotifier.event(v, "This will open a dialog with MarkDown Support", EventNotifier.LENGTH_SHORT);
        });

        initializeBottomSearch();

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

        navController = Navigation.findNavController(this, R.id.mainNavHost);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.getInt("id") != -1) {
            user = userDao.getUserByID(preference.getInt("id"));
            setUserImageComponents(user);
        }
    }

    private void rememberMe() {
        preference = new PrivatePreference(Main.this);
        if (preference.getInt("id") == -1) {
            preference.clear();
            startActivity(new Intent(Main.this, Starting.class));
            finish();
            return;
        }
        UserController.statusCheck(user.getId(), Main.this, new IUserStatus() {
            @Override
            public void onSuccess(User user) {
                Main.this.user = user;
            }

            @Override
            public void onExit(String message) {
                preference.clear();
                database.userDao().deleteUserByID(user.getId());
                Toast.makeText(Main.this, message, Toast.LENGTH_LONG).show();
                startActivity(new Intent(Main.this, Starting.class));
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(Main.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserComponents() {
        ImageButton moreAcc = navigationHeader.findViewById(R.id.navHeaderMore);
        TextView username = navigationHeader.findViewById(R.id.navHeaderUsername);
        TextView email = navigationHeader.findViewById(R.id.navHeaderEmail);
        TextView appVer = findViewById(R.id.navFooterAppVer);

        setUserImageComponents(user);

        username.setText(user.getUsername());
        email.setText(user.getEmail());
        appVer.setText(BuildConfig.VERSION_NAME);

        moreAcc.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = mainRoot.getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            MoreAccounts moreAccounts = new MoreAccounts(params);
            moreAccounts.show(getSupportFragmentManager(), "moreAccountsFrag");
        });
    }

    private void setUserImageComponents(User user) {
        ShapeableImageView imageView = navigationHeader.findViewById(R.id.navHeaderImage);

        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .apply(new RequestOptions().override(50, 50))
                .load(user.getImageUrl())
                .skipMemoryCache(true)
                .into(imageView);


        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .apply(new RequestOptions().override(70, 70))
                .load(user.getImageUrl())
                .skipMemoryCache(true)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        toolbar.setNavigationIcon(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void prepareForBugListing() {
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

                    IssueController.uploadBug(user.getId(), desc, device, checkBox.isChecked(), getApplicationContext(), new IVolley() {
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
                    IssueController.uploadFeature(user.getId(), desc, checkBox.isChecked(), getApplicationContext(), new IVolley() {
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

    private void initializeBottomSearch() {
        searchRecycler = bottomSheetConstraint.findViewById(R.id.searchRecycler);
        searchedUserNotFound = bottomSheetConstraint.findViewById(R.id.searchUsersNotFound);

        List<SearchedUser> searchedUserList = new ArrayList<>();
        adapterSearch = new AdapterSearch(searchedUserList, user,
                pos -> FriendController.sendFriendRequest(user.getId(), adapterSearch.getSearchedUserID(pos), getApplicationContext(), new IVolley() {
                    @Override
                    public void onSuccess(String message) {
                        EventNotifier.customEvent(toolbar, R.drawable.ic_check_24, message);
                    }

                    @Override
                    public void onError(String message) {
                        EventNotifier.errorEvent(toolbar, message);
                    }
                }));

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
                if (dy >= 0 && layoutManager.findLastCompletelyVisibleItemPosition() >= adapterSearch.getItemCount() - 1) {
                    fetchRows();
                }
            }

            private void fetchRows() {
                adapterSearch.setOffset(adapterSearch.getOffset() + 10);
                FriendController.searchUser(user, searchView.getQuery().toString().trim(), adapterSearch.getOffset(), Main.this, new ISearch() {
                    @Override
                    public void onSuccess(List<SearchedUser> searchedUserList) {
                        searchedUserNotFound.setVisibility(View.GONE);
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
        getMenuInflater().inflate(R.menu.main_toolbar_chat, menu);

        MenuItem menuItem = menu.findItem(R.id.mainToolbarItemSearch);
        MenuItem menuItem1 = menu.findItem(R.id.mainToolbarItemNotifications);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setQueryHint(getString(R.string.searchUserQuery));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterSearch.setOffset(0);
                if (!query.isEmpty()) {
                    FriendController.searchUser(user, query.trim(), adapterSearch.getOffset(), Main.this, new ISearch() {
                        @Override
                        public void onSuccess(List<SearchedUser> searchedUserList) {
                            searchedUserNotFound.setVisibility(View.GONE);
                            adapterSearch.refillList(searchedUserList);
                        }

                        @Override
                        public void onError(String message) {
                            adapterSearch.clearListAndRefreshAdapter();
                            searchedUserNotFound.setVisibility(View.VISIBLE);
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
                searchedUserNotFound.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
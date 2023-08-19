package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.CachedQuery;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterCachedProfile;
import com.github.fearmygaze.mercury.view.adapter.AdapterCachedQuery;
import com.github.fearmygaze.mercury.view.adapter.AdapterSearch;
import com.github.fearmygaze.mercury.view.fragment.Home;
import com.github.fearmygaze.mercury.view.fragment.Notifications;
import com.github.fearmygaze.mercury.view.fragment.People;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView navigation;

    MaterialCardView profile, search, settings;
    ShapeableImageView profileImage;
    TextView appName;

    User user;

    //Search
    BottomSheetBehavior<ConstraintLayout> searchBehaviour;
    EditText searchBox;
    AdapterCachedProfile adapterCachedPr;
    AdapterCachedQuery adapterCachedQuery;
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        frameLayout = findViewById(R.id.mainFrame);
        navigation = findViewById(R.id.mainBottomNavigation);

        profile = findViewById(R.id.mainProfileImageParent);
        profileImage = findViewById(R.id.mainProfileImage);
        appName = findViewById(R.id.mainAppName);
        search = findViewById(R.id.mainSearch);
        settings = findViewById(R.id.mainSettings);

        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.mainOptionHome) {
                replaceFragment(Home.newInstance(user), "home");
                return true;
            } else if (item.getItemId() == R.id.mainOptionPeople) {
                replaceFragment(People.newInstance(user.getId()), "people");
                return true;
            } else if (item.getItemId() == R.id.mainOptionNotifications) {
                replaceFragment(Notifications.newInstance(user), "people");
                return true;
            }
            return false;
        });

        rememberMe(savedInstanceState);
        searchSheet();

        navigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.mainOptionHome) {
                Toast.makeText(Main.this, "We need to refresh or go to the top", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.mainOptionPeople) {

            } else {

            }
        });

        profile.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Profile.class)
                    .putExtra(User.ID, user.getId()));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        search.setOnClickListener(v -> searchBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED));

        settings.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class)
                    .putExtra("user", user));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    @Override
    protected void onResume() {
        FirebaseUser oldFireUser = FirebaseAuth.getInstance().getCurrentUser();
        adapterCachedPr.set(AppDatabase.getInstance(Main.this).cachedProfile().getAll());
        adapterCachedQuery.set(AppDatabase.getInstance(Main.this).cachedQueries().getAll());
        Auth.rememberMe(oldFireUser, Main.this, new OnUserResponseListener() {
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
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (searchBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            searchBox.clearFocus();
            searchBox.setText("");
            searchBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (navigation.getSelectedItemId() == R.id.mainOptionHome) {
                super.onBackPressed();
            } else {
                navigation.setSelectedItemId(R.id.mainOptionHome);
            }
        }

    }

    private void searchSheet() {
        ConstraintLayout parent = findViewById(R.id.searchSheetParent);
        searchBehaviour = BottomSheetBehavior.from(parent);

        //General
        ShapeableImageView close = findViewById(R.id.searchGoBack);
        MaterialCardView clear = findViewById(R.id.searchClear);
        ShapeableImageView clearCache = findViewById(R.id.searchCachedClear);
        Group profileGroup = findViewById(R.id.searchCachedProfilesGroup);
        Group queryGroup = findViewById(R.id.searchCachedQueryGroup);

        //Search
        searchBox = findViewById(R.id.searchBox);
        RecyclerView searchRecycler = findViewById(R.id.searchRecycler);
        PagingConfig config = new PagingConfig(3, 15, true);
        FirestorePagingOptions<User> searchOptions = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(Auth.searchQuery("@"), config, User.class)
                .build();

        AdapterSearch adapterSearch = new AdapterSearch(user, searchOptions, searchRecycler, Main.this, () -> {
            searchBox.setText("");
            searchBox.clearFocus();
        });

        searchRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.VERTICAL, false));
        searchRecycler.setAdapter(adapterSearch);

        clear.setOnClickListener(v -> searchBox.setText(""));
        close.setOnClickListener(v -> onBackPressed());

        //Cached Profiles
        adapterCachedPr = new AdapterCachedProfile(profileGroup, user, Main.this);
        RecyclerView prRecycler = findViewById(R.id.searchCachedProfileRecycler);
        prRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.HORIZONTAL, false));
        prRecycler.setAdapter(adapterCachedPr);

        //Cached Queries
        RecyclerView qrRecycler = findViewById(R.id.searchCachedQueryRecycler);
        adapterCachedQuery = new AdapterCachedQuery(queryGroup, Main.this, searchBox::setText);
        qrRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.VERTICAL, false));
        qrRecycler.setAdapter(adapterCachedQuery);

        //Clear Recent searches
        clearCache.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setTitle(R.string.dialogDeleteAllCachedTitle)
                    .setMessage(R.string.dialogDeleteAllCachedMessage)
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(R.string.generalClear, (dialog, i) -> {
                        adapterCachedPr.clear();
                        adapterCachedQuery.clear();
                    })
                    .show();
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                if (query.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                    profileGroup.setVisibility(View.GONE);
                    queryGroup.setVisibility(View.GONE);
                } else {
                    profileGroup.setVisibility(View.VISIBLE);
                    queryGroup.setVisibility(View.VISIBLE);
                    clear.setVisibility(View.GONE);
                    searchHandler.removeCallbacks(searchRunnable);
                    adapterSearch.updateOptions(searchOptions);
                    searchRecycler.setVisibility(View.GONE);
                }

                if (editable.toString().length() >= 3) {
                    searchRecycler.setVisibility(View.VISIBLE);
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> {
                        adapterSearch.updateOptions(new FirestorePagingOptions.Builder<User>()
                                .setLifecycleOwner(Main.this)
                                .setQuery(Auth.searchQuery(query), config, User.class)
                                .build());
                        adapterCachedQuery.set(new CachedQuery(query));
                    };
                    searchHandler.postDelayed(searchRunnable, 350);
                }
            }
        });
        //TODO: When the return is empty show the error
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
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment, tag);
        fragmentTransaction.commit();
    }

    private void rememberMe(Bundle bundle) {
        String id = Tools.getStrPreference("current", Main.this);
        if (id != null) {
            User oldUser = AppDatabase.getInstance(Main.this).userDao().getUserByUserID(id);
            if (oldUser != null) {
                user = oldUser;
                Tools.profileImage(user.getImage(), Main.this).into(profileImage);
                if (bundle == null) {
                    navigation.setSelectedItemId(R.id.mainOptionHome);
                }
            }
        }
    }

}

package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.github.fearmygaze.mercury.firebase.dao.AuthDao;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.CachedQuery;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterCachedProfile;
import com.github.fearmygaze.mercury.view.adapter.AdapterCachedQuery;
import com.github.fearmygaze.mercury.view.adapter.AdapterSearch;
import com.github.fearmygaze.mercury.view.fragment.Home;
import com.github.fearmygaze.mercury.view.fragment.People;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class Main extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView navigation;
    FloatingActionButton createRoom;

    MaterialCardView profile, search, settings;
    ShapeableImageView profileImage;
    TextView appName;

    User user;
    AppDatabase database;

    //Search Sheet
    ConstraintLayout searchSheetParent;
    BottomSheetBehavior<ConstraintLayout> searchBehaviour;
    Group cachedComp;
    ShapeableImageView searchSheetClose;
    MaterialCardView searchBoxClear;
    ShapeableImageView clearCache;

    //Cached Profiles
    AdapterCachedProfile adapterCachedProfile;
    RecyclerView cachedProfileRecycler;

    //Cached Searches
    AdapterCachedQuery adapterCachedQuery;
    RecyclerView cachedSearchRecycler;

    //Search
    EditText searchBox;
    RecyclerView searchRecycler;
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        database = AppDatabase.getInstance(Main.this);

        frameLayout = findViewById(R.id.mainFrame);
        navigation = findViewById(R.id.mainBottomNavigation);
        createRoom = findViewById(R.id.mainChatCreate);

        profile = findViewById(R.id.mainProfileImageParent);
        profileImage = findViewById(R.id.mainProfileImage);
        appName = findViewById(R.id.mainAppName);
        search = findViewById(R.id.mainSearch);
        settings = findViewById(R.id.mainSettings);

        //Search sheet
        searchSheetParent = findViewById(R.id.searchSheetParent);
        cachedComp = findViewById(R.id.searchCachedComps);
        searchSheetClose = findViewById(R.id.searchGoBack);
        searchBox = findViewById(R.id.searchBox);
        searchBoxClear = findViewById(R.id.searchClear);
        clearCache = findViewById(R.id.searchCachedClear);
        searchRecycler = findViewById(R.id.searchRecycler);

        //Cached Profile
        cachedProfileRecycler = findViewById(R.id.searchCachedProfileRecycler);
        //Cached Query
        cachedSearchRecycler = findViewById(R.id.searchCachedQueryRecycler);

        rememberMe(savedInstanceState);

        //Search Sheet
        searchBehaviour = BottomSheetBehavior.from(searchSheetParent);
        searchSheetClose.setOnClickListener(v -> onBackPressed());
        searchBoxClear.setOnClickListener(v -> searchBox.setText(""));

        //Cached Profile
        adapterCachedProfile = new AdapterCachedProfile(user, Main.this, count -> handleCachedComps());
        cachedProfileRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.HORIZONTAL, false));
        cachedProfileRecycler.setAdapter(adapterCachedProfile);
        cachedProfileRecycler.setItemAnimator(null);

        //Cached Search
        adapterCachedQuery = new AdapterCachedQuery(Main.this, new AdapterCachedQuery.CQueryListener() {
            @Override
            public void send(String str) {
                searchBox.setText(str);
            }

            @Override
            public void getCount(int count) {
                handleCachedComps();
            }
        });
        cachedSearchRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.VERTICAL, false));
        cachedSearchRecycler.setAdapter(adapterCachedQuery);
        cachedSearchRecycler.setItemAnimator(null);

        clearCache.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setTitle(R.string.dialogDeleteAllCachedTitle)
                    .setMessage(R.string.dialogDeleteAllCachedMessage)
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(R.string.generalClear, (dialog, i) -> {
                        adapterCachedProfile.clear();
                        adapterCachedQuery.clear();
                        handleCachedComps();
                    }).show();
        });

        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.mainOptionChat) {
                createRoom.setVisibility(View.VISIBLE);
                replaceFragment(Home.newInstance(user), "home");
                return true;
            } else if (item.getItemId() == R.id.mainOptionFriends) {
                createRoom.setVisibility(View.GONE);
                replaceFragment(People.newInstance(user), "people");
                return true;
            }
            return false;
        });

        searchSheet();

        profile.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Profile.class)
                    .putExtra(User.PARCEL, user));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        search.setOnClickListener(v -> {
            handleCachedComps();
            searchBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        settings.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class)
                    .putExtra(User.PARCEL, user));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        createRoom.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, RoomCreator.class)
                    .putExtra(User.PARCEL, user));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Auth.rememberMe(AuthDao.getUser(), Main.this, new OnUserResponseListener() {
            @Override
            public void onSuccess(int code, User data) {
                switch (code) {
                    case 0:
                        user = data;
                        Tools.profileImage(user.getImage(), Main.this).into(profileImage);
                        break;
                    case 1:
                        AuthDao.signOut();
                        Toast.makeText(Main.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main.this, Starting.class));
                        finish();
                        break;
                    case 2:
                        AuthDao.signOut();
                        Toast.makeText(Main.this, "You need to activate your account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main.this, Starting.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                AuthDao.signOut();
                Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Main.this, SignIn.class));
                finish();
            }
        });
        handleCachedComps();
    }

    @Override
    public void onBackPressed() {
        if (searchBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            searchBox.clearFocus();
            searchBox.setText("");
            Tools.closeKeyboard(Main.this);
            searchBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (navigation.getSelectedItemId() == R.id.mainOptionChat) {
                super.onBackPressed();
            } else {
                navigation.setSelectedItemId(R.id.mainOptionChat);
            }
        }

    }

    private void searchSheet() {
        //Search
        PagingConfig config = new PagingConfig(3, 15, false);
        FirestorePagingOptions<User> searchOptions = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(Auth.searchQuery("@"), config, User.class)
                .build();
        ConstraintLayout errorLayout = findViewById(R.id.searchErrorLayout);
        TextView errorText = findViewById(R.id.searchErrorText);

        AdapterSearch adapterSearch = new AdapterSearch(user, searchOptions, searchRecycler, Main.this, () -> {
            searchBox.setText("");
            searchBox.clearFocus();
            handleCachedComps();
        });

        searchRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.VERTICAL, false));
        searchRecycler.setAdapter(adapterSearch);

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
                    searchBoxClear.setVisibility(View.VISIBLE);
                    cachedComp.setVisibility(View.GONE);
                    cachedProfileRecycler.setVisibility(View.GONE);
                    cachedSearchRecycler.setVisibility(View.GONE);
                } else {
                    searchBoxClear.setVisibility(View.GONE);
                    searchHandler.removeCallbacks(searchRunnable);
                    handleCachedComps();
                    adapterSearch.updateOptions(searchOptions);
                    searchRecycler.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
                if (query.length() >= 3) {
                    searchRecycler.setVisibility(View.VISIBLE);
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> adapterSearch.updateOptions(new FirestorePagingOptions.Builder<User>()
                            .setLifecycleOwner(Main.this)
                            .setQuery(Auth.searchQuery(query), config, User.class)
                            .build());
                    searchHandler.postDelayed(searchRunnable, 350);
                }
                searchBox.setOnEditorActionListener((textView, actionID, keyEvent) -> {
                    if (actionID == EditorInfo.IME_ACTION_SEARCH && query.length() >= 3) {
                        adapterCachedQuery.set(new CachedQuery(query));
                        Tools.closeKeyboard(Main.this);
                        return true;
                    }
                    return false;
                });
            }
        });

        adapterSearch.addLoadStateListener(states -> {
            LoadState append = states.getAppend();
            if (append instanceof LoadState.NotLoading) {
                LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                if (notLoading.getEndOfPaginationReached()) {
                    Log.d("customLog", "finished loading");
                    Log.d("customLog", adapterSearch.getItemCount() + "");
                    if (searchRecycler.getVisibility() == View.VISIBLE) {
                        if (adapterSearch.getItemCount() > 0) {
                            errorLayout.setVisibility(View.GONE);
                            searchRecycler.setVisibility(View.VISIBLE);
                        } else {
                            errorLayout.setVisibility(View.VISIBLE);
                            searchRecycler.setVisibility(View.GONE);
                        }
                    }
                    return null;
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
            User oldUser = User.getRoomUser(id, Main.this);
            if (oldUser != null) {
                user = oldUser;
                Tools.profileImage(user.getImage(), Main.this).into(profileImage);
                if (bundle == null) {
                    navigation.setSelectedItemId(R.id.mainOptionChat);
                }
            }
        }
    }

    private void handleCachedComps() {
        adapterCachedProfile.set(database.cachedProfile().getAll());
        adapterCachedQuery.set(database.cachedQueries().getAll());

        if (adapterCachedProfile.getItemCount() > 0 || adapterCachedQuery.getItemCount() > 0) {
            cachedComp.setVisibility(View.VISIBLE);
        } else {
            cachedComp.setVisibility(View.GONE);
        }

        if (adapterCachedQuery.getItemCount() == 0) {
            cachedSearchRecycler.setVisibility(View.GONE);
        } else {
            cachedSearchRecycler.setVisibility(View.VISIBLE);
        }

        if (adapterCachedProfile.getItemCount() == 0) {
            cachedProfileRecycler.setVisibility(View.GONE);
        } else {
            cachedProfileRecycler.setVisibility(View.VISIBLE);
        }
    }

}

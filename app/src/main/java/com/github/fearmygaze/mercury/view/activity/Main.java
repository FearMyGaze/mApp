package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.custom.CustomLinearLayout;
import com.github.fearmygaze.mercury.custom.UIAction;
import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.model.PrevSearch;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.Search;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.IFireCallback;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.adapter.AdapterPrevQueries;
import com.github.fearmygaze.mercury.view.adapter.AdapterSearch;
import com.github.fearmygaze.mercury.view.adapter.AdapterVisitedProfiles;
import com.github.fearmygaze.mercury.view.fragment.Home;
import com.github.fearmygaze.mercury.view.fragment.People;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class Main extends AppCompatActivity {

    //MainContent
    FrameLayout frameLayout;

    //AppBar
    MaterialCardView profile;
    ShapeableImageView profileImage, createRoomBtn, searchBtn, settingsBtn;

    //BottomAppBar
    BottomNavigationView navigation;

    //Utils
    User1 user1;
    RoomDB roomDB;

    //Search Sheet
    ConstraintLayout searchSheetParent;
    BottomSheetBehavior<ConstraintLayout> searchBehaviour;
    Group cachedComp;
    ShapeableImageView searchSheetClose;
    MaterialCardView searchBoxClear;
    ShapeableImageView clearCache;
    AdapterSearch adapterSearch;

    //Cached Profiles
    AdapterVisitedProfiles adapterVisitedProfiles;
    RecyclerView cachedProfileRecycler;

    //Cached Searches
    AdapterPrevQueries adapterPrevQueries;
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

        roomDB = RoomDB.getInstance(Main.this);

        frameLayout = findViewById(R.id.mainFrame);

        profile = findViewById(R.id.mainProfileImageParent);
        profileImage = findViewById(R.id.mainProfileImage);
        createRoomBtn = findViewById(R.id.mainCreate);
        searchBtn = findViewById(R.id.mainSearch);
        settingsBtn = findViewById(R.id.mainSettings);

        //BottomNavigation
        navigation = findViewById(R.id.mainBottomNavigation);

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

        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.mainOptionChat) {
                replaceFragment(Home.newInstance(user1), "home");
                return true;
            } else if (id == R.id.mainOptionSearch) {

                return true;
            } else if (id == R.id.mainOptionFriends) {
                replaceFragment(People.newInstance(user1), "people");
                return true;
            }
            return false;
        });
        rememberMe(savedInstanceState);
        navigation.setOnItemReselectedListener(item -> {
            //This stops the reloading the comps
        });

        //Search Sheet
        searchBehaviour = BottomSheetBehavior.from(searchSheetParent);
        searchSheetClose.setOnClickListener(v -> onBackPressed());
        searchBoxClear.setOnClickListener(v -> searchBox.setText(""));

        //Cached Profile
        adapterVisitedProfiles = new AdapterVisitedProfiles(user1, Main.this, count -> handleCachedComps());
        cachedProfileRecycler.setLayoutManager(new CustomLinearLayout(Main.this, LinearLayoutManager.HORIZONTAL, false));
        cachedProfileRecycler.setAdapter(adapterVisitedProfiles);
        cachedProfileRecycler.setItemAnimator(null);

        //Cached Search
        adapterPrevQueries = new AdapterPrevQueries(Main.this, new AdapterPrevQueries.CQueryListener() {
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
        cachedSearchRecycler.setAdapter(adapterPrevQueries);
        cachedSearchRecycler.setItemAnimator(null);

        clearCache.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setTitle(R.string.dialogDeleteAllCachedTitle)
                    .setMessage(R.string.dialogDeleteAllCachedMessage)
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(R.string.generalClear, (dialog, i) -> {
                        adapterVisitedProfiles.clear();
                        adapterPrevQueries.clear();
                        handleCachedComps();
                    }).show();
        });

        searchSheet();

        profile.setOnClickListener(v -> {
//            startActivity(new Intent(Main.this, MyProfile.class)
//                    .putExtra(User1.PARCEL, user1));
            startActivity(new Intent(Main.this, Test.class));
        });

        profile.setOnLongClickListener(v -> true);

        createRoomBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, RoomCreator.class)
                    .putExtra(User1.PARCEL, user1));
        });

        searchBtn.setOnClickListener(v -> {
            handleCachedComps();
            searchBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class)
                    .putExtra(User1.PARCEL, user1));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Auth(Main.this).rememberMe(new CallBackResponse<User1>() {
            @Override
            public void onSuccess(User1 data) {
                user1 = data;
                Tools.profileImage(user1.getImage(), Main.this).into(profileImage);
            }

            @Override
            public void onError(String message) {
                this.onFailure(message);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Main.this, Starting.class));
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
            UIAction.closeKeyboard(Main.this);
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
        ConstraintLayout errorLayout = findViewById(R.id.searchErrorLayout);
        TextView errorText = findViewById(R.id.searchErrorText);

        adapterSearch = new AdapterSearch(new User1(), () -> searchBox.clearFocus());
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
                if (!query.isEmpty()) {
                    searchBoxClear.setVisibility(View.VISIBLE);
                    cachedComp.setVisibility(View.GONE);
                    cachedProfileRecycler.setVisibility(View.GONE);
                    cachedSearchRecycler.setVisibility(View.GONE);
                } else {
                    searchBoxClear.setVisibility(View.GONE);
                    searchHandler.removeCallbacks(searchRunnable);
                    handleCachedComps();
                    adapterSearch.clear();
                    searchRecycler.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
                if (query.length() >= 3) {
                    searchRecycler.setVisibility(View.VISIBLE);
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> new Search(Main.this).search(query, new IFireCallback<List<User1>>() {
                        @Override
                        public void onSuccess(List<User1> result) {
                            if (!result.isEmpty()) {
                                adapterSearch.add(result);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                adapterSearch.clear();
                                errorLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(String message) {
                            //TODO: Add here a message that will look like this
                            // `Wait we didnt find that user either you misspelled
                            // their name or for some reason they dont use our app what a waste of their time`
                            Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    searchHandler.postDelayed(searchRunnable, 350);
                }
                searchBox.setOnEditorActionListener((textView, actionID, keyEvent) -> {
                    if (actionID == EditorInfo.IME_ACTION_SEARCH && query.length() >= 3) {
                        adapterPrevQueries.set(new PrevSearch(user1.getId(), query));
                        UIAction.closeKeyboard(Main.this);
                        return true;
                    }
                    return false;
                });
            }
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
            User1 oldUser = roomDB.users().getByID(id);
            if (oldUser != null) {
                user1 = oldUser;
                Tools.profileImage(user1.getImage(), Main.this).into(profileImage);
                if (bundle == null) {
                    navigation.setSelectedItemId(R.id.mainOptionChat);
                }
            }
        }
    }

    private void handleCachedComps() {
        adapterVisitedProfiles.set(roomDB.profiles().getAll());
        adapterPrevQueries.set(roomDB.searches().getAll());

        if (adapterSearch.getItemCount() == 0) {
            cachedComp.setVisibility(adapterVisitedProfiles.getItemCount() > 0 || adapterPrevQueries.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            cachedSearchRecycler.setVisibility(adapterPrevQueries.getItemCount() == 0 ? View.GONE : View.VISIBLE);
            cachedProfileRecycler.setVisibility(adapterVisitedProfiles.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    }

}

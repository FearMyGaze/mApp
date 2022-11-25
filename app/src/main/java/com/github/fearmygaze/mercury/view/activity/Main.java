package com.github.fearmygaze.mercury.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.fearmygaze.mercury.BuildConfig;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.controller.FriendController;
import com.github.fearmygaze.mercury.controller.IssueController;
import com.github.fearmygaze.mercury.controller.UserController;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    ConstraintLayout mainRoot;

    ConstraintLayout navbarLayout;
    ImageButton navbarButton;
    EditText navBarSearch;
    ShapeableImageView navBarImage;

    BottomNavigationView bottomNavigationView;

    BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    ConstraintLayout bottomSheetConstraint;
    AdapterSearch adapterSearch;
    RecyclerView searchRecycler;
    TextView searchedUserNotFound;

    NavController navController;

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
        mainRoot = findViewById(R.id.mainRoot);
        navbarLayout = findViewById(R.id.mainNavbarLayout);
        navbarButton = findViewById(R.id.mainNavBarSettingsButton);
        navBarSearch = findViewById(R.id.mainNavbarSearchBox);
        navBarImage = findViewById(R.id.mainNavbarProfileButton);
        bottomNavigationView = findViewById(R.id.mainBottomNavigation);
        bottomSheetConstraint = findViewById(R.id.search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        preference = new PrivatePreference(Main.this);
        userDao = database.userDao();

        if (preference.contains("id") && preference.getInt("id") != -1) {//TODO: We need to call the user components here or find a way to not enter the setUserComponents()
            user = userDao.getUserByID(preference.getInt("id"));
            setUserImageComponents(user);
            rememberMe();
            navigationBar();
            initializeBottomSearch();
        } else {
            preference.clearAllValues();
            startActivity(new Intent(Main.this, Starting.class));
            finish();
        }

        navController = Navigation.findNavController(this, R.id.mainNavHost);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.contains("id") && preference.getInt("id") != -1) {
            user = userDao.getUserByID(preference.getInt("id"));
            setUserImageComponents(user);
        }
    }

    private void rememberMe() {
        preference = new PrivatePreference(Main.this);
        if (preference.getInt("id") == -1) {
            preference.clearAllValues();
            startActivity(new Intent(Main.this, Starting.class));
            finish();
            return;
        }
        UserController.statusCheck(user.getId(), Main.this, new IUserStatus() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onExit(String message) {
                preference.clearAllValues();
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

    private void setUserImageComponents(User user) {
        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .apply(new RequestOptions().override(70, 70))
                .load(user.getImageUrl())
                .into(navBarImage);
    }

    private void navigationBar() {
        navbarButton.setOnClickListener(v -> dialogAppSettings());
        navBarImage.setOnClickListener(v -> dialogProfile());
        navBarSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                navBarSearch.setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {//TODO: Add Here the search functionality
                        if (!navBarSearch.getText().toString().trim().isEmpty()) {
                            adapterSearch.setOffset(0);
                            FriendController.searchUser(user, v1.getText().toString().trim(), adapterSearch.getOffset(), Main.this, new ISearch() {
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
                        } else {
                            searchedUserNotFound.setVisibility(View.VISIBLE);
                            adapterSearch.clearListAndRefreshAdapter();
                        }
                        return true;
                    }
                    return false;
                });
            }
        });
    }

    private void dialogBug() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bug);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().getAttributes().y = (int) (navbarLayout.getHeight() * 1.2);
        dialog.getWindow().setAttributes(layoutParams);

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

    private void dialogFeature() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_feature);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().getAttributes().y = (int) (navbarLayout.getHeight() * 1.2);
        dialog.getWindow().setAttributes(layoutParams);

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

    private void dialogProfile() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_profile);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().getAttributes().y = (int) (navbarLayout.getHeight() * 1.2);
        dialog.getWindow().setAttributes(layoutParams);

        ImageButton exitButton = dialog.findViewById(R.id.dialogProfileExit);
        ShapeableImageView userImage = dialog.findViewById(R.id.dialogProfileUserImage);
        TextView username = dialog.findViewById(R.id.dialogProfileUsername);
        TextView email = dialog.findViewById(R.id.dialogProfileUserEmail);
        ConstraintLayout moreOptions = dialog.findViewById(R.id.dialogProfileCurrentProfile);
        MaterialButton manageAccount = dialog.findViewById(R.id.dialogProfileManageAccount);
        Group manageAccGroup = dialog.findViewById(R.id.dialogProfileManageAccountGroup);
        ConstraintLayout switchAccount = dialog. findViewById(R.id.dialogProfileManageAccountSwitchAccount);
        ConstraintLayout addAccount = dialog.findViewById(R.id.dialogProfileManageAccountAddAccount);
        ConstraintLayout removeAccount = dialog.findViewById(R.id.dialogProfileManageAccountRemoveAccount);
        ConstraintLayout signOut = dialog.findViewById(R.id.dialogProfileSignOut);
        TextView termsOfService = dialog.findViewById(R.id.dialogProfileTos);
        TextView version = dialog.findViewById(R.id.dialogProfileVersion);
        TextView changeLog = dialog.findViewById(R.id.dialogProfileChangelog);

        termsOfService.setOnClickListener(v -> Toast.makeText(this, "This will show the TOS of the app", Toast.LENGTH_SHORT).show());
        changeLog.setOnClickListener(v -> Toast.makeText(this, "This will show the changes of the app", Toast.LENGTH_SHORT).show());

        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .apply(new RequestOptions().override(70, 70))
                .load(user.getImageUrl())
                .skipMemoryCache(true)
                .into(userImage);
        username.setText(user.getUsername());
        email.setText(user.getEmail());

        moreOptions.setOnClickListener(v -> {
            if (manageAccGroup.getVisibility() == View.VISIBLE){
                manageAccGroup.setVisibility(View.GONE);
            }else{
                manageAccGroup.setVisibility(View.VISIBLE);
            }
        });
        manageAccount.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Profile.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            dialog.dismiss();
        });
        signOut.setOnClickListener(v -> {//TODO: We need to find a way to hold the data of the other users and then we have to
            Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
        });
        switchAccount.setOnClickListener(v -> {

        });
        removeAccount.setOnClickListener(v -> {

        });
        addAccount.setOnClickListener(v -> {

        });
        version.setText(BuildConfig.VERSION_NAME);
        exitButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void dialogAppSettings() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_app_settings);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().getAttributes().y = (int) (navbarLayout.getHeight() * 1.2);
        dialog.getWindow().setAttributes(layoutParams);

        ImageButton exit = dialog.findViewById(R.id.dialogAppSettingsExit);
        ConstraintLayout settings = dialog.findViewById(R.id.dialogAppSettingsSettings);
        ConstraintLayout feature = dialog.findViewById(R.id.dialogAppSettingsFeature);
        ConstraintLayout bug = dialog.findViewById(R.id.dialogAppSettingsBug);

        settings.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            dialog.dismiss();
        });
        feature.setOnClickListener(v -> {
            dialog.dismiss();
            dialogFeature();
        });
        bug.setOnClickListener(v -> {
            dialog.dismiss();
            dialogBug();
        });

        exit.setOnClickListener(v -> dialog.dismiss());
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
                        Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
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
                if (!navBarSearch.getText().toString().trim().isEmpty()){
                    adapterSearch.setOffset(adapterSearch.getOffset() + 10);
                    FriendController.searchUser(user, navBarSearch.getText().toString(), adapterSearch.getOffset(), Main.this, new ISearch() {
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (navBarSearch.hasFocus()){
            navBarSearch.setText("");
            adapterSearch.clearListAndRefreshAdapter();
            searchedUserNotFound.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            navBarSearch.clearFocus();
        }else{
            super.onBackPressed();
        }
    }
}
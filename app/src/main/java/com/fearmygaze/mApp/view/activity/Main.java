package com.fearmygaze.mApp.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.SearchedUser;
import com.fearmygaze.mApp.util.TextHandler;
import com.fearmygaze.mApp.view.adapter.AdapterSearch;
import com.fearmygaze.mApp.view.fragment.Chat;
import com.fearmygaze.mApp.view.fragment.Friends;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    FirebaseUser user;

    boolean notifications = true;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.mainDrawer);
        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigation);
        navigationView = findViewById(R.id.mainNavigation);

        setSupportActionBar(toolbar);

        /*
        * BottomSheet
        * */

        bottomSheetConstraint = findViewById(R.id.search);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
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
                case R.id.navigationMenuItemTerms:
                    Toast.makeText(this, "This will open a dialog", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigationMenuItemSignOut:
                    Toast.makeText(this, "This will sign out the user", Toast.LENGTH_SHORT).show();
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

        friends = new Friends();
        chat = new Chat();

        replaceFragment(chat);
        bottomNavigationView.setSelectedItemId(R.id.mainNavigationItemChoice1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
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

    private void prepareForBugListing() { //TODO: Pass as an extra value the device model and the user data
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bug);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputLayout dialogBugDescError = dialog.findViewById(R.id.dialogBugDescError);
        TextInputEditText dialogBugDesc = dialog.findViewById(R.id.dialogBugDesc);
        MaterialButton dialogBugButton = dialog.findViewById(R.id.dialogBugConfirm);

        dialogBugDesc.addTextChangedListener(new TextHandler(dialogBugDescError));
                
        dialogBugButton.setOnClickListener(v -> {
            if (Objects.requireNonNull(dialogBugDesc.getText()).toString().length() > 250){
                dialogBugDescError.setError(getText(R.string.dialogBugError));
            }else{
                Toast.makeText(this, "Error posted... !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                drawerLayout.close();
            }
        });
        dialog.show();

    }

    private void initializeToolbar(MaterialToolbar toolbar){ //TODO: We need to optimize the glide stuff and the order of operations
        /*This add the icon of the user*/
        Glide.with(this)
                .asDrawable()
                .circleCrop()
                .placeholder(R.drawable.ic_person_24)
                .load("https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png")
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
            if (item.getItemId() == R.id.mainToolbarItemNotifications){
                if (notifications){
                    notifications = false;
                    item.setIcon(R.drawable.ic_notifications_off_24);
                }else{
                    notifications = true;
                    item.setIcon(R.drawable.ic_notifications_active_24);
                }
            }
            return true;
        });

    }

    private void initializeBottomSearch(){
        RecyclerView searchRecycler = bottomSheetConstraint.findViewById(R.id.searchRecycler);
        TextView userNotFound = bottomSheetConstraint.findViewById(R.id.searchUsersNotFound);

        sheetBehavior = BottomSheetBehavior.from(bottomSheetConstraint);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setExpandedOffset(1);
        sheetBehavior.setSkipCollapsed(true);

        List<SearchedUser> searchedUserList = new ArrayList<>();

        searchedUserList.add(new SearchedUser("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        searchedUserList.add(new SearchedUser("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        searchedUserList.add(new SearchedUser("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        searchedUserList.add(new SearchedUser("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));
        searchedUserList.add(new SearchedUser("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png","Niko"));

        if (searchedUserList.size() > 0){
            userNotFound.setVisibility(View.GONE);
        }

        adapterSearch = new AdapterSearch(searchedUserList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(layoutManager);
        searchRecycler.setAdapter(adapterSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.mainToolbarItemSearch);
        MenuItem menuItem1 = menu.findItem(R.id.mainToolbarItemNotifications);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showToast(query,0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3){
                    showToast(newText,1); //TODO: MAKE THINGS WORK
                }
                return true;
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
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }

    private void showToast(String message, int duration){
        Toast.makeText(this, message, duration).show();
    }
}
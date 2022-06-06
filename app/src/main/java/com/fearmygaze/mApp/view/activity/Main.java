package com.fearmygaze.mApp.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.view.fragment.People;
import com.fearmygaze.mApp.view.fragment.Chat;
import com.fearmygaze.mApp.view.fragment.Search;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Main extends AppCompatActivity {

    public Fragment friends, chat, search;

    DrawerLayout drawerLayout;

    MaterialToolbar toolbar;

    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;

    View header;

    boolean notifications = true;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.mainDrawer);
        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigation);
        navigationView = findViewById(R.id.mainNavigation);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.navigationMenuItemProfile:
                    startActivity(new Intent(Main.this, Profile.class));
                    return true;
                case R.id.navigationMenuItemNotifications:
                    startActivity(new Intent(Main.this, Notifications.class));
                    return true;
                case R.id.navigationMenuItemSettings:
                    startActivity(new Intent(Main.this, Settings.class));
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

        /*
        * BottomNavigation
        * */

        friends = new People();
        chat = new Chat();
        search = new Search();

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
                case R.id.mainNavigationItemChoice3:
                    replaceFragment(search);
                    return true;
                default:
                    return false;
            }
        });
    }
    
    private void initializeToolbar(MaterialToolbar toolbar){
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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


}
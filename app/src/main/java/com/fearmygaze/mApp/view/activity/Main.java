package com.fearmygaze.mApp.view.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import com.fearmygaze.mApp.view.fragment.Friends;
import com.fearmygaze.mApp.view.fragment.Home;
import com.fearmygaze.mApp.view.fragment.Search;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Main extends AppCompatActivity {

    public Fragment friends, home, search;

    DrawerLayout drawerLayout;

    MaterialToolbar toolbar;

    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;

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

       // setSupportActionBar(toolbar); //If i use this then the button is not showing

        //TODO: Clear up the main (just create a method with the setting up of the toolbar)

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main.this, "Ahh niko ahh", Toast.LENGTH_SHORT).show();
//                navigationView.getMenu().clear();
//                navigationView.inflateMenu(R.menu.main_navigation);

            }
        });

        Glide.with(this)
                .asDrawable()
                .circleCrop()
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

        /*
        * BottomNavigation
        * */

        friends = new Friends();
        home = new Home();
        search = new Search();

        replaceFragment(home);
        bottomNavigationView.setSelectedItemId(R.id.mainNavigationItemHome);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.mainNavigationItemFriends:
                    replaceFragment(friends);
                    return true;
                case R.id.mainNavigationItemHome:
                    replaceFragment(home);
                    return true;
                case R.id.mainNavigationItemSearch:
                    replaceFragment(search);
                    return true;
                default:
                    return false;
            }
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
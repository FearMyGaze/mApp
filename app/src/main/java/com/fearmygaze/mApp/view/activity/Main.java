package com.fearmygaze.mApp.view.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.adapter.AdapterConversation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;

    MaterialToolbar toolbar;

    RecyclerView recyclerViewCon;

    AdapterConversation adapterConversation;

    ConstraintLayout behaviourLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.mainRefresh);
        toolbar = findViewById(R.id.mainToolbar);
        recyclerViewCon = findViewById(R.id.mainConversation);

        setSupportActionBar(toolbar);

        behaviourLayout = findViewById(R.id.bottomSheetDialogRoot);

        TypedValue tv = new TypedValue();

        int actionBarHeight = 0;

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

        ViewGroup.LayoutParams params = behaviourLayout.getLayoutParams();
        params.height = getResources().getDisplayMetrics().heightPixels - actionBarHeight;
        behaviourLayout.setLayoutParams(params);

        BottomSheetBehavior<ConstraintLayout> behavior = BottomSheetBehavior.from(behaviourLayout);
        behavior.setSkipCollapsed(true);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    refreshLayout.setEnabled(false);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    refreshLayout.setEnabled(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuItem1){
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return false;
        });


        List<Conversation> conversationList = new ArrayList<>();

        Conversation conversation = new Conversation("1","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "Username","is this a bool ??","22:20");
        Conversation conversation2 = new Conversation("2","https://static-cdn.jtvnw.net/jtv_user_pictures/fl0m-profile_image-efa66f8f4aa42f40-70x70.png",
                "Username","is this a String ??","22:20");
        Conversation conversation3 = new Conversation("3","https://static-cdn.jtvnw.net/jtv_user_pictures/0d5d4ba9-881f-4d04-a9ae-b1ebe618442d-profile_image-70x70.png",
                "Username","is this an int ??","22:20");

        conversationList.add(conversation);
        conversationList.add(conversation2);
        conversationList.add(conversation3);



        adapterConversation = new AdapterConversation(conversationList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Main.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCon.setLayoutManager(layoutManager);
        recyclerViewCon.setAdapter(adapterConversation);




        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.mainMenuItem1);
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
                    showToast(newText,1);
                }
                return true;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                behaviourLayout = findViewById(R.id.bottomSheetDialogRoot);

                BottomSheetBehavior<ConstraintLayout> behavior = BottomSheetBehavior.from(behaviourLayout);

                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                return true;
            }
        });

        return true;
    }

    private void showToast(String message, int duration){
        Toast.makeText(this, message, duration).show();
    }
}
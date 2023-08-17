package com.github.fearmygaze.mercury.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagingConfig;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.fragment.Home;
import com.github.fearmygaze.mercury.view.fragment.People;
import com.github.fearmygaze.mercury.view.fragment.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView navigation;

    MaterialCardView profile, settings;
    ShapeableImageView profileImage;
    TextView appName;

    User user;

    EditText searchBar;
    FirestorePagingOptions<User> emptyOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.mainFrame);
        navigation = findViewById(R.id.mainBottomNavigation);

        profile = findViewById(R.id.mainProfileImageParent);
        profileImage = findViewById(R.id.mainProfileImage);
        appName = findViewById(R.id.mainAppName);
        searchBar = findViewById(R.id.mainSearchBar);
        settings = findViewById(R.id.mainSettings);

        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.mainOptionHome) {
                appName.setVisibility(View.VISIBLE);
                searchBar.setText("");
                searchBar.setVisibility(View.GONE);
                replaceFragment(Home.newInstance(user));
                return true;
            } else if (item.getItemId() == R.id.mainOptionSearch) {
                appName.setVisibility(View.GONE);
                searchBar.setText("");
                searchBar.setVisibility(View.VISIBLE);
                emptyOption = new FirestorePagingOptions.Builder<User>()
                        .setLifecycleOwner(Main.this)
                        .setQuery(Auth.searchQuery("@"), new PagingConfig(3, 15), User.class)
                        .build();
                replaceFragment(Search.newInstance(user));
                return true;
            } else if (item.getItemId() == R.id.mainOptionPeople) {
                appName.setVisibility(View.VISIBLE);
                searchBar.setText("");
                searchBar.setVisibility(View.GONE);
                replaceFragment(People.newInstance(user.getId()));
                return true;
            }
            return false;
        });

        rememberMe(savedInstanceState);

        navigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.mainOptionHome) {
                Toast.makeText(Main.this, "We need to refresh or go to the top", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.mainOptionSearch) {

            } else if (item.getItemId() == R.id.mainOptionPeople) {

            }
        });

        profile.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Profile.class)
                    .putExtra(User.ID, user.getId()));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        settings.setOnClickListener(v -> {
            startActivity(new Intent(Main.this, Settings.class)
                    .putExtra("user", user));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }

    public EditText getSearchBar() {
        return searchBar;
    }

    public FirestorePagingOptions<User> getEmptyOption() {
        return emptyOption;
    }

    private void rememberMe(Bundle bundle) {
        FirebaseUser oldFireUser = FirebaseAuth.getInstance().getCurrentUser();
        if (oldFireUser != null) {
            User oldUser = AppDatabase.getInstance(Main.this).userDao().getUserByUserID(oldFireUser.getUid());
            if (oldUser != null) {
                user = oldUser;
                Tools.profileImage(user.getImage(), Main.this).into(profileImage);
                if (bundle == null) {
                    navigation.setSelectedItemId(R.id.mainOptionHome);
                }
            }
        } else FirebaseAuth.getInstance().signOut();

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
    }

}

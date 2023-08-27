package com.github.fearmygaze.mercury.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class AdapterSearch extends FirestorePagingAdapter<User, AdapterSearch.SearchVH> {

    User myUser;
    RecyclerView recyclerView;
    Activity activity;

    private final SearchAdapter listener;

    public AdapterSearch(User user, @NonNull FirestorePagingOptions<User> options, RecyclerView recyclerView, Activity activity, SearchAdapter listener) {
        super(options);
        this.myUser = user;
        this.recyclerView = recyclerView;
        this.activity = activity;
        this.listener = listener;
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @NonNull
    @Override
    public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVH holder, int position, @NonNull User model) {
        Tools.profileImage(model.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(model.getUsername());
        holder.status.setText(model.getStatus());
        holder.root.setOnClickListener(v -> {
            listener.onClick();
            AppDatabase.getInstance(v.getContext()).cachedProfile().insert(new Profile(model.getId(), model.getUsername(), model.getImage()));
            Tools.goToProfileViewer(myUser, model, v.getContext());
        });
    }

    public static class SearchVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username, status;

        public SearchVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterUserSearchRoot);
            image = itemView.findViewById(R.id.adapterUserSearchImage);
            username = itemView.findViewById(R.id.adapterUserSearchUsername);
            status = itemView.findViewById(R.id.adapterUserSearchStatus);
        }
    }

    public interface SearchAdapter {
        void onClick();
    }
}

package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.SearchVH> {
    User user;
    List<User> search;
    SearchActions actions;

    public AdapterSearch(User user, SearchActions actions) {
        this.user = user;
        this.search = new ArrayList<>();
        this.actions = actions;
    }

    @NonNull
    @Override
    public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVH holder, int position) {
        User model = search.get(holder.getAbsoluteAdapterPosition());
        Tools.profileImage(model.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(model.getUsername());
        holder.status.setText(model.getBio());
        holder.root.setOnClickListener(v -> {
            Profile.insertToCache(v.getContext(), model);
            actions.onClick();
            Tools.goToProfileViewer(user, model, v.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return search.size();
    }

    public void set(List<User> list) {
        if (list.size() >= 1) {
            search.addAll(list);
            notifyItemRangeChanged(0, list.size());
        } else {
            clear();
        }
    }

    public void add(List<User> list) {
        if (list.size() >= 1) {
            search.clear();
            search.addAll(list);
            notifyItemRangeChanged(0, list.size());
        } else {
            clear();
        }
    }

    public void clear() {
        notifyItemRangeRemoved(0, search.size());
        search.clear();
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

    public interface SearchActions {
        void onClick();
    }
}

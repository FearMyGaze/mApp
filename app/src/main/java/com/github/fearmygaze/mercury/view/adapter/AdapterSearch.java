package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.SearchVH> {
    private final User1 user1;
    private final List<User1> searchResult;
    private final ISearchActions actions;

    public AdapterSearch(User1 user1, ISearchActions actions) {
        this.user1 = user1;
        this.searchResult = new ArrayList<>();
        this.actions = actions;
    }

    @NonNull
    @Override
    public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVH holder, int position) {
        User1 model = searchResult.get(holder.getAbsoluteAdapterPosition());
        Tools.profileImage(model.getImage(), holder.itemView.getContext()).into(holder.image);
        holder.username.setText(model.getUsername());
        holder.bio.setText(model.getBio());
        holder.root.setOnClickListener(v -> {
//            Context ctx = v.getContext();
//            Database.getInstance(ctx).profiles().insert(user1.getId(), model);
//            actions.onClick();
//            ctx.startActivity(new Intent(ctx, ProfileViewer.class)
//                    .putExtra("user", user1)
//                    .putExtra("otherUser", model));
        });
    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public void set(List<User1> list) {
        if (!list.isEmpty()) {
            searchResult.addAll(list);
            notifyItemRangeChanged(0, list.size());
        } else {
            clear();
        }
    }

    public void add(List<User1> list) {
        if (!list.isEmpty()) {
            searchResult.clear();
            searchResult.addAll(list);
            notifyItemRangeChanged(0, list.size());
        } else {
            clear();
        }
    }

    public void clear() {
        notifyItemRangeRemoved(0, searchResult.size());
        searchResult.clear();
    }

    public static class SearchVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username, bio;

        public SearchVH(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.adapterSearchRoot);
            image = itemView.findViewById(R.id.adapterSearchImage);
            username = itemView.findViewById(R.id.adapterSearchUsername);
            bio = itemView.findViewById(R.id.adapterSearchBio);
        }
    }

    public interface ISearchActions {
        void onClick();
    }
}

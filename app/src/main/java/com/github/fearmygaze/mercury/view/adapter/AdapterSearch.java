package com.github.fearmygaze.mercury.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    List<User> users;

    public AdapterSearch(List<User> userList) {
        this.users = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(holder.itemView.getRootView()).load(users.get(position).imageURL).centerInside().into(holder.image); //TODO: Add Placeholder
        holder.name.setText(users.get(position).name);
        holder.username.setText(users.get(position).username);
        holder.root.setOnClickListener(v ->
                v.getContext().startActivity(new Intent(v.getContext(), ProfileViewer.class)
                        .putExtra("userUID", users.get(position).userUID)
                        .putExtra("name", users.get(position).name))
        );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> list) {
        users = list;
        notifyItemRangeChanged(0, users.size());
    }

    public void clearUsers() {
        notifyItemRangeRemoved(0, users.size());
        users.clear();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView name, username;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterSearchRoot);
            image = itemView.findViewById(R.id.adapterSearchImage);
            name = itemView.findViewById(R.id.adapterSearchName);
            username = itemView.findViewById(R.id.adapterSearchUsername);
        }
    }
}
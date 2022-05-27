package com.fearmygaze.mApp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.SearchedUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    List<SearchedUser> searchedUserList;

    public AdapterSearch(List<SearchedUser> searchedUserList) {
        this.searchedUserList = searchedUserList;
    }

    @NonNull
    @Override
    public AdapterSearch.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearch.MyViewHolder holder, int position) {

        String id = searchedUserList.get(position).getId();
        String image = searchedUserList.get(position).getImage();
        String username = searchedUserList.get(position).getUsername();
        String name = searchedUserList.get(position).getName();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);

        holder.username.setText(username);
        holder.name.setText(name);

        holder.root.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Clicked" + username, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return searchedUserList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout root;
        ShapeableImageView image;
        MaterialTextView name, username;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.adapterSearchRoot);
            image = itemView.findViewById(R.id.adapterSearchImage);
            name = itemView.findViewById(R.id.adapterSearchName);
            username = itemView.findViewById(R.id.adapterSearchUsername);
        }
    }
}

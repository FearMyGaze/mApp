package com.fearmygaze.mApp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.model.miniUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    List<miniUser> miniUserList;

    public AdapterSearch(List<miniUser> miniUserList) {
        this.miniUserList = miniUserList;
    }

    @NonNull
    @Override
    public AdapterSearch.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearch.MyViewHolder holder, int position) {

        String id = miniUserList.get(position).getId();
        String image = miniUserList.get(position).getImage();
        String username = miniUserList.get(position).getUsername();
        String name = miniUserList.get(position).getName();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);

        holder.username.setText(username);
        holder.name.setText(name);

        holder.root.setOnClickListener(view -> {

        });
    }

    public List<miniUser> getMiniUserList() {
        return miniUserList;
    }

    @Override
    public int getItemCount() {
        return miniUserList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        FragmentContainerView root;
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

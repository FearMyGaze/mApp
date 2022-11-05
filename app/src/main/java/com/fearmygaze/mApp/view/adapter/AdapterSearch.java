package com.fearmygaze.mApp.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.ISearchAdapter;
import com.fearmygaze.mApp.model.SearchedUser;
import com.fearmygaze.mApp.model.User1;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    List<SearchedUser> searchedUserList;
    User1 user;
    private final ISearchAdapter iSearchAdapter;
    private int offset;

    public AdapterSearch(List<SearchedUser> searchedUserList, User1 user, ISearchAdapter iSearchAdapter) {
        this.searchedUserList = searchedUserList;
        this.user = user;
        this.iSearchAdapter = iSearchAdapter;
        this.offset = 0;
    }

    @NonNull
    @Override
    public AdapterSearch.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false), iSearchAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearch.MyViewHolder holder, int position) {

        int id = searchedUserList.get(position).getId();
        String image = searchedUserList.get(position).getImage();
        String username = searchedUserList.get(position).getUsername();
        boolean friend = searchedUserList.get(position).isFriend();

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.overrideOf(70, 70))
                .into(holder.image);


        holder.username.setText(username);

        if (friend) {
            holder.button.setVisibility(View.INVISIBLE);
        }
    }

    public int getSearchedUserID(int pos){
        return searchedUserList.get(pos).getId();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refillList(List<SearchedUser> searchedUserList) {
        this.searchedUserList = searchedUserList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearListAndRefreshAdapter() {
        this.searchedUserList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addResultAndRefreshAdapter(List<SearchedUser> searchedUserList) {
        this.searchedUserList.addAll(searchedUserList);
        notifyDataSetChanged();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getItemCount() {
        return searchedUserList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        MaterialTextView username;
        MaterialButton button;

        public MyViewHolder(@NonNull View itemView, ISearchAdapter iSearchAdapter) {
            super(itemView);

            image = itemView.findViewById(R.id.adapterSearchImage);
            username = itemView.findViewById(R.id.adapterSearchUsername);
            button = itemView.findViewById(R.id.adapterSearchButton);

            button.setOnClickListener(v -> {
                if (iSearchAdapter != null){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        iSearchAdapter.onItemClicked(pos);
                    }
                }
            });
        }
    }
}
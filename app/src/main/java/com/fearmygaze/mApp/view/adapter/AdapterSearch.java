package com.fearmygaze.mApp.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fearmygaze.mApp.Controller.FriendController;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.model.SearchedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    List<SearchedUser> searchedUserList;
    private int offset;


    public AdapterSearch(List<SearchedUser> searchedUserList) {
        this.searchedUserList = searchedUserList;
        this.offset = 0;
    }

    @NonNull
    @Override
    public AdapterSearch.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearch.MyViewHolder holder, int position) {

        int id = searchedUserList.get(position).getId();
        String image = searchedUserList.get(position).getImage();
        String username = searchedUserList.get(position).getUsername();
        boolean friend = searchedUserList.get(position).isFriend();

//        Glide.with(holder.itemView.getRootView())
//                .load(image)
//                .placeholder(R.drawable.ic_launcher_background)
//                .circleCrop()
//                .apply(RequestOptions.centerCropTransform())
//                .into(holder.image);

        holder.username.setText(username);

        if (friend) {
            holder.button.setVisibility(View.INVISIBLE);
        }

        holder.button.setOnClickListener(v -> {
            FriendController.sendFriendRequest(20, id, v.getContext(), new IVolley() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        });
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


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.adapterSearchImage);
            username = itemView.findViewById(R.id.adapterSearchUsername);
            button = itemView.findViewById(R.id.adapterSearchButton);
        }
    }
}
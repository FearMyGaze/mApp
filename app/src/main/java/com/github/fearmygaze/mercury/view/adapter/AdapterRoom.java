package com.github.fearmygaze.mercury.view.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_REGULAR = 0, TYPE_GROUP = 1;
    int type, id;


    public AdapterRoom(int type, int id) {
        this.type = type;
        this.id = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type == 0) {
            return null;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class RoomRegularVH extends RecyclerView.ViewHolder {
        protected RoomRegularVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class RoomGroupVH extends RecyclerView.ViewHolder {
        protected RoomGroupVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}

package com.fearmygaze.mApp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IMoreAccountsAdapter;
import com.fearmygaze.mApp.model.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterMoreAccounts extends RecyclerView.Adapter<AdapterMoreAccounts.MyViewHolder> {

    List<User> userList;
    int currentUser;

    private final IMoreAccountsAdapter iMoreAccountsAdapter;

    public AdapterMoreAccounts(List<User> userList, int currentUser, IMoreAccountsAdapter iMoreAccountsAdapter) {
        this.userList = userList;
        this.currentUser = currentUser;
        this.iMoreAccountsAdapter = iMoreAccountsAdapter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_more_accounts, parent, false), iMoreAccountsAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(holder.itemView.getRootView())
                .load(userList.get(position).getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .centerCrop()
                .apply(new RequestOptions().override(70, 70))
                .into(holder.image);

        holder.username.setText(userList.get(position).getUsername());
        holder.email.setText(userList.get(position).getEmail());
        holder.root.setEnabled(false);

        if (currentUser != userList.get(position).getId()) {
            holder.root.setEnabled(true);
            holder.currentAcc.setVisibility(View.INVISIBLE);
        }
    }

    public User getUser(int pos){
        return userList.get(pos);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout root;
        ShapeableImageView image;
        TextView username, email;
        ImageButton currentAcc;

       public MyViewHolder(@NonNull View itemView, IMoreAccountsAdapter iMoreAccountsAdapter) {
           super(itemView);
           root = itemView.findViewById(R.id.adapterMoreAccountsFrame);
           image = itemView.findViewById(R.id.adapterMoreAccountsImage);
           username = itemView.findViewById(R.id.adapterMoreAccountsUsername);
           email = itemView.findViewById(R.id.adapterMoreAccountsEmail);
           currentAcc = itemView.findViewById(R.id.adapterMoreAccountsCurrent);

           root.setOnClickListener(v-> {
               if (iMoreAccountsAdapter != null){
                   int pos = getAdapterPosition();
                   if (pos != RecyclerView.NO_POSITION){
                       iMoreAccountsAdapter.onActionClick(pos);
                   }
               }
           });
       }
   }
}
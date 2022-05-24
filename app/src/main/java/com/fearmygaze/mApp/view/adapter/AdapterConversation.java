package com.fearmygaze.mApp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.MyViewHolder> {

    List<Conversation> conversations;

    public AdapterConversation(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversations, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String id = conversations.get(position).getId();
        String image = conversations.get(position).getImage();
        String username = conversations.get(position).getUsername();
        String lastMessage = conversations.get(position).getLastMessage();
        String time = conversations.get(position).getLastMessageTime();


        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);

        holder.username.setText(username);
        holder.lastMessage.setText(lastMessage);
        holder.time.setText(time);

        holder.frameLayout.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "asd", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout frameLayout;
        ShapeableImageView image;
        MaterialTextView username, lastMessage, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            frameLayout = itemView.findViewById(R.id.adapterConversationRoot);
            image = itemView.findViewById(R.id.adapterConversationImage);
            username = itemView.findViewById(R.id.adapterConversationUsername);
            lastMessage = itemView.findViewById(R.id.adapterConversationLastMessage);
            time = itemView.findViewById(R.id.adapterConversationTime);
        }
    }
}

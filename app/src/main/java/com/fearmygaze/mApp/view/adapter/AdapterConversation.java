package com.fearmygaze.mApp.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.activity.ChatRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.MyViewHolder> {

    List<Conversation> conversations;
    Activity activity;

    public AdapterConversation(List<Conversation> conversations , Activity activity) {
        this.conversations = conversations;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversations, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
            Intent intent = new Intent(activity, ChatRoom.class);
            intent.putExtra("username",username);
            view.getContext().startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        holder.frameLayout.setOnLongClickListener(view -> {
            Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.dialog_conversation);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            MaterialButton optionDelete = dialog.findViewById(R.id.dialogConversationDelete);
            MaterialButton optionNotification = dialog.findViewById(R.id.dialogConversationNotification);
            MaterialButton optionReport = dialog.findViewById(R.id.dialogConversationReport);

            optionDelete.setOnClickListener(v -> {
                conversations.remove(holder.getAdapterPosition());
                notifyItemChanged(holder.getAdapterPosition());
                dialog.dismiss();
            });

            optionNotification.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Notification", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            optionReport.setOnClickListener(v -> {
                String reportedUser = conversations.get(position).getUsername();
                dialog.dismiss();
            });

            dialog.show();

            return true;
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

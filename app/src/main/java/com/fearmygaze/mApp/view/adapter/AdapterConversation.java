package com.fearmygaze.mApp.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.interfaces.IConversationAdapter;
import com.fearmygaze.mApp.model.Conversation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import github.com.st235.lib_swipetoactionlayout.ActionBindHelper;
import github.com.st235.lib_swipetoactionlayout.SwipeAction;
import github.com.st235.lib_swipetoactionlayout.SwipeMenuListener;
import github.com.st235.lib_swipetoactionlayout.SwipeToActionLayout;

@SuppressLint("ALL")
public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.MyViewHolder> {

    List<Conversation> conversations;
    Activity activity;
    private int offset;
    private final IConversationAdapter iConversationAdapter;
    private final ActionBindHelper actionBindHelper;

    public AdapterConversation(List<Conversation> conversations, Activity activity, IConversationAdapter iConversationAdapter) {
        this.conversations = conversations;
        this.activity = activity;
        this.offset = 0;
        this.iConversationAdapter = iConversationAdapter;
        this.actionBindHelper = new ActionBindHelper();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversations, parent, false), iConversationAdapter, actionBindHelper);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String image = conversations.get(position).getImage();
        String username = conversations.get(position).getUsername();
        String lastMessage = conversations.get(position).getLastMessage();
        String time = conversations.get(position).getLastMessageTime();

        actionBindHelper.bind(conversations.get(position).getUsername(), holder.actionLayout);

        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .centerCrop()
                .apply(new RequestOptions().override(70, 70))
                .into(holder.image);

        holder.username.setText(username);
        holder.lastMessage.setText(lastMessage);
        holder.time.setText(time);

        holder.root.setOnLongClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.dialog_conversation);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            MaterialButton optionDeleteConversation = dialog.findViewById(R.id.dialogConversationDelete);
            MaterialButton optionRemoveFriend = dialog.findViewById(R.id.dialogConversationRemove);
            MaterialButton optionReportUser = dialog.findViewById(R.id.dialogConversationReport);

            optionDeleteConversation.setOnClickListener(v1 -> {
                iConversationAdapter.onDeleteConversation(holder.getAdapterPosition());
                dialog.dismiss();
            });

            optionRemoveFriend.setOnClickListener(v1 -> {
                iConversationAdapter.onRemoveFriend(holder.getAdapterPosition());
                dialog.dismiss();
            });

            optionReportUser.setOnClickListener(v1 -> {
                iConversationAdapter.onReportUser(holder.getAdapterPosition());
                dialog.dismiss();
            });

            dialog.show();
            return true;
        });

    }
    //TODO: Simplify the names oof the methods in all the adapters
    public void refillList( List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    public void addResultAndRefreshAdapter( List<Conversation> conversations) {
        this.conversations.addAll(conversations);
        notifyDataSetChanged();
    }

    public void clearListAndRefreshAdapter() {
        this.conversations.clear();
        notifyDataSetChanged();
    }

    public void removeItemAndRefresh(int pos) {
        this.conversations.remove(pos);
        notifyItemRemoved(pos);
    }

    public Conversation getConversation(int pos){
        return conversations.get(pos);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        SwipeToActionLayout actionLayout;
        ConstraintLayout root;
        ShapeableImageView image;
        MaterialTextView username, lastMessage, time;

        public MyViewHolder(@NonNull View itemView, IConversationAdapter iConversationAdapter, ActionBindHelper actionBindHelper) {
            super(itemView);
            actionLayout = itemView.findViewById(R.id.adapterConversationActionLayout);
            root = itemView.findViewById(R.id.adapterConversationRoot);
            image = itemView.findViewById(R.id.adapterConversationImage);
            username = itemView.findViewById(R.id.adapterConversationUsername);
            lastMessage = itemView.findViewById(R.id.adapterConversationLastMessage);
            time = itemView.findViewById(R.id.adapterConversationTime);

            root.setOnClickListener(v -> {
                if (iConversationAdapter != null) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        iConversationAdapter.onConversation(getAdapterPosition());
                    }
                }
            });

            actionLayout.setMenuListener(new SwipeMenuListener() {
                @Override
                public void onClosed(@NonNull View view) {
                    root.setEnabled(true);
                }

                @Override
                public void onOpened(@NonNull View view) {
                    root.setEnabled(false);
                    actionBindHelper.closeOtherThan(username.getText().toString());
                }

                @Override
                public void onFullyOpened(@NonNull View view, @NonNull SwipeAction swipeAction) {

                }

                @Override
                public void onActionClicked(@NonNull View view, @NonNull SwipeAction swipeAction) {
                    switch (swipeAction.getActionId()) {
                        case R.id.swipeActionOptionDelete:
                            if (iConversationAdapter != null) {
                                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                    iConversationAdapter.onDeleteConversation(getAdapterPosition());
                                }
                            }
                            break;
                        case R.id.swipeActionOptionRemove:
                            if (iConversationAdapter != null) {
                                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                    iConversationAdapter.onRemoveFriend(getAdapterPosition());
                                }
                            }
                            break;
                        case R.id.swipeActionOptionReport:
                            if (iConversationAdapter != null) {
                                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                    iConversationAdapter.onReportUser(getAdapterPosition());
                                }
                            }
                            break;
                    }
                }
            });
        }
    }
}
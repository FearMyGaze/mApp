package com.github.fearmygaze.mercury.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.model.Message;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

public class AdapterMessage extends FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder> {

    String userID;
    RecyclerView recyclerView;

    public AdapterMessage(@NonNull FirebaseRecyclerOptions<Message> options, String userID, RecyclerView recyclerView) {
        super(options);
        this.userID = userID;
        this.recyclerView = recyclerView;
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new SelfTextVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_self_text, parent, false));
        }
        return new OtherTextVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other_text, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message model) {

    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex);
        switch (type) {
            case ADDED:
                this.recyclerView.scrollToPosition(getItemCount() - 1);
                notifyItemInserted(newIndex);
                break;
            case CHANGED:
                Objects.requireNonNull(this.recyclerView.getItemAnimator()).setChangeDuration(0);
                Objects.requireNonNull(this.recyclerView.getItemAnimator()).setMoveDuration(150);
                notifyItemChanged(newIndex);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(getItem(position).sendByID, userID) ? 0 : 1;
    }

    protected static class SelfTextVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        TextView content, date;

        public SelfTextVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.messageTextSelfRoot);
            content = itemView.findViewById(R.id.messageTextSelfContent);
            date = itemView.findViewById(R.id.messageTextSelfDate);
        }
    }

    protected static class SelfImageVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ConstraintLayout hiddenContent;
        ShapeableImageView content;
        TextView date;

        public SelfImageVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.messageImageSelfRoot);
            hiddenContent = itemView.findViewById(R.id.messageImageSelfHiddenContent);
            content = itemView.findViewById(R.id.messageImageSelfContent);
            date = itemView.findViewById(R.id.messageImageSelfDate);
            if (Tools.getPreference("showImage", itemView.getContext())) {
                content.setVisibility(View.GONE);
                hiddenContent.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.VISIBLE);
                hiddenContent.setVisibility(View.GONE);
            }
        }
    }

    protected static class SelfSoundVH extends RecyclerView.ViewHolder {
        public SelfSoundVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected static class OtherTextVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        TextView content, date;

        public OtherTextVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected static class OtherImageVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ConstraintLayout hiddenContent;
        ShapeableImageView content;
        TextView date;

        public OtherImageVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.messageImageOtherRoot);
            hiddenContent = itemView.findViewById(R.id.messageImageOtherHiddenContent);
            content = itemView.findViewById(R.id.messageImageOtherContent);
            date = itemView.findViewById(R.id.messageImageOtherDate);
            if (Tools.getPreference("showImage", itemView.getContext())) {
                content.setVisibility(View.GONE);
                hiddenContent.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.VISIBLE);
                hiddenContent.setVisibility(View.GONE);
            }
        }
    }

    protected static class OtherSoundVH extends RecyclerView.ViewHolder {
        public OtherSoundVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}

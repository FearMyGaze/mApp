package com.github.fearmygaze.mercury.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.RoomDB;
import com.github.fearmygaze.mercury.database.dao.VisitedProfileDao;
import com.github.fearmygaze.mercury.database.model.VisitedProfile;
import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.Search;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.github.fearmygaze.mercury.view.util.ProfileViewer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterVisitedProfiles extends RecyclerView.Adapter<AdapterVisitedProfiles.VisitedProfilesVH> {
    User1 user1;
    Context context;
    VisitedProfileDao database;
    ItemInterface listener;
    List<VisitedProfile> visitedProfiles;

    public AdapterVisitedProfiles(User1 user1, Context context, ItemInterface listener) {
        this.database = RoomDB.getInstance(context).profiles();
        this.visitedProfiles = database.getAll();
        this.user1 = user1;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VisitedProfilesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VisitedProfilesVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_visited_profiles, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VisitedProfilesVH holder, int position) {
        Tools.profileImage(visitedProfiles.get(holder.getAbsoluteAdapterPosition()).getImage(), holder.image.getContext()).into(holder.image);
        holder.username.setText(visitedProfiles.get(holder.getAbsoluteAdapterPosition()).getUsername());
        holder.root.setOnClickListener(v -> {
            new Search(v.getContext()).getUserById(visitedProfiles.get(holder.getAbsoluteAdapterPosition()).getId(), new CallBackResponse<User1>() {
                @Override
                public void onSuccess(User1 object) {
                    v.getContext().startActivity(new Intent(context, ProfileViewer.class)
                            .putExtra(User.PARCEL, user1)
                            .putExtra(User.PARCEL_OTHER, object));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        holder.root.setOnLongClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setMessage(String.format("%s %s %s",
                            v.getContext().getString(R.string.dialogDeleteCachedPart1),
                            visitedProfiles.get(holder.getAbsoluteAdapterPosition()).getUsername(),
                            v.getContext().getString(R.string.dialogDeleteCachedPart2)))
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(v.getContext().getText(R.string.generalClear), (dialog, i) -> clear(holder.getAbsoluteAdapterPosition()))
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return visitedProfiles.size();
    }

    public void set(List<VisitedProfile> list) {
        if (list != null) {
            visitedProfiles = list;
            notifyItemRangeChanged(0, visitedProfiles.size());
        }
    }

    public void clear(int pos) {
        notifyItemRemoved(pos);
        database.delete(visitedProfiles.get(pos));
        visitedProfiles.remove(pos);
        listener.getCount(visitedProfiles.size());
    }

    public void clear() {
        notifyItemRangeRemoved(0, visitedProfiles.size());
        database.deleteAll();
        visitedProfiles.clear();
        listener.getCount(0);
    }

    public static class VisitedProfilesVH extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        ShapeableImageView image;
        TextView username;

        public VisitedProfilesVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterCachedSearchRoot);
            image = itemView.findViewById(R.id.adapterCachedSearchImage);
            username = itemView.findViewById(R.id.adapterCachedSearchUsername);
        }
    }

    public interface ItemInterface {
        void getCount(int count);
    }
}

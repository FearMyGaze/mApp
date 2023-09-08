package com.github.fearmygaze.mercury.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.database.CProfileDao;
import com.github.fearmygaze.mercury.firebase.Auth;
import com.github.fearmygaze.mercury.firebase.interfaces.OnUserResponseListener;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.User;
import com.github.fearmygaze.mercury.util.Tools;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterCachedProfile extends RecyclerView.Adapter<AdapterCachedProfile.CachedProfilesVH> {

    List<Profile> profiles;
    User user;
    Context context;
    CProfileDao database;
    private final ItemInterface listener;

    public AdapterCachedProfile(User user, Context context, ItemInterface listener) {
        this.database = AppDatabase.getInstance(context).cachedProfile();
        this.profiles = database.getAll();
        this.user = user;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CachedProfilesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CachedProfilesVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cached_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CachedProfilesVH holder, int position) {
        Tools.profileImage(profiles.get(holder.getAbsoluteAdapterPosition()).getImage(), holder.image.getContext()).into(holder.image);
        holder.username.setText(profiles.get(holder.getAbsoluteAdapterPosition()).getUsername());
        holder.root.setOnClickListener(v -> Auth.getUserProfile(
                profiles.get(holder.getAbsoluteAdapterPosition()).getId(), v.getContext(), new OnUserResponseListener() {
                    @Override
                    public void onSuccess(int code, User otherUser) {
                        if (code == 0) {
                            Tools.goToProfileViewer(user, otherUser, v.getContext());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                })
        );
        holder.root.setOnLongClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setMessage(String.format("%s %s %s",
                            v.getContext().getString(R.string.dialogDeleteCachedPart1),
                            profiles.get(holder.getAbsoluteAdapterPosition()).getUsername(),
                            v.getContext().getString(R.string.dialogDeleteCachedPart2)))
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(v.getContext().getText(R.string.generalClear), (dialog, i) -> clear(holder.getAbsoluteAdapterPosition()))
                    .show();
            return true;
        });
    }

    public void set(List<Profile> list) {
        if (list != null) {
            profiles = list;
            notifyItemRangeChanged(0, profiles.size());
        }
    }

    public void clear(int pos) {
        notifyItemRemoved(pos);
        database.delete(profiles.get(pos));
        profiles.remove(pos);
        listener.getCount(profiles.size());
    }

    public void clear() {
        notifyItemRangeRemoved(0, profiles.size());
        database.deleteAll();
        profiles.clear();
        listener.getCount(0);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    protected static class CachedProfilesVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        ShapeableImageView image;
        TextView username;

        public CachedProfilesVH(@NonNull View itemView) {
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

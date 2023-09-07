package com.github.fearmygaze.mercury.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fearmygaze.mercury.R;
import com.github.fearmygaze.mercury.database.AppDatabase;
import com.github.fearmygaze.mercury.database.CQueriesDao;
import com.github.fearmygaze.mercury.model.CachedQuery;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterCachedQuery extends RecyclerView.Adapter<AdapterCachedQuery.CachedQueryVH> {

    List<CachedQuery> queries;
    Context context;
    CQueriesDao database;
    CQueryListener listener;

    public AdapterCachedQuery(Context context, CQueryListener listener) {
        this.database = AppDatabase.getInstance(context).cachedQueries();
        this.queries = database.getAll();
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CachedQueryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CachedQueryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cached_query, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CachedQueryVH holder, int position) {
        holder.search.setText(queries.get(holder.getAbsoluteAdapterPosition()).getQuery());
        holder.root.setOnClickListener(v -> listener.send(holder.search.getText().toString()));
        holder.root.setOnLongClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setMessage(String.format("%s %s %s",
                            v.getContext().getString(R.string.dialogDeleteCachedPart1),
                            queries.get(holder.getAbsoluteAdapterPosition()).getQuery(),
                            v.getContext().getString(R.string.dialogDeleteCachedPart2)))
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(v.getContext().getText(R.string.generalClear), (dialog, i) -> clear(holder.getAbsoluteAdapterPosition()))
                    .show();
            return true;
        });
    }

    public void set(CachedQuery query) {
        int oldSize = queries.size();
        database.insert(query);
        queries = database.getAll();
        notifyItemRangeChanged(oldSize, queries.size());
    }

    public void set(List<CachedQuery> list) {
        if (list != null && list.size() > 1) {
            queries = list;
            notifyItemRangeChanged(0, queries.size());
        }
    }

    public void clear(int pos) {
        notifyItemRemoved(pos);
        database.delete(queries.get(pos));
        queries.remove(pos);
        listener.getCount(queries.size());
    }

    public void clear() {
        notifyItemRangeRemoved(0, queries.size());
        database.deleteAll();
        queries.clear();
        listener.getCount(0);
    }

    @Override
    public int getItemCount() {
        return queries.size();
    }

    protected static class CachedQueryVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        TextView search;
        ImageView image;

        public CachedQueryVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterCachedQueryRoot);
            search = itemView.findViewById(R.id.adapterCachedQueryText);
            image = itemView.findViewById(R.id.adapterCachedQueryImage);
        }
    }

    public interface CQueryListener {
        void send(String str);

        void getCount(int count);
    }
}

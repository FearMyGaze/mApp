package com.github.fearmygaze.mercury.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.Group;
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
    Group group;
    Context context;
    CQueryListener listener;

    public AdapterCachedQuery(Group group, Context context, CQueryListener listener) {
        this.queries = AppDatabase.getInstance(context).cachedQueries().getAll();
        this.group = group;
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
        holder.query.setText(queries.get(holder.getAbsoluteAdapterPosition()).getQuery());
        holder.root.setOnClickListener(v -> listener.send(holder.query.getText().toString()));
        holder.root.setOnLongClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
            builder.setBackground(AppCompatResources.getDrawable(v.getContext(), R.color.basicBackground))
                    .setMessage(v.getContext().getString(R.string.dialogDeleteCachedPart1) + " " + queries.get(position).getQuery() + " " + v.getContext().getString(R.string.dialogDeleteCachedPart2))
                    .setNegativeButton(R.string.generalCancel, (dialog, i) -> dialog.dismiss())
                    .setPositiveButton(v.getContext().getText(R.string.generalClear), (dialog, i) -> clear(holder.getAbsoluteAdapterPosition()))
                    .show();
            return true;
        });
    }

    public void set(CachedQuery query) {
        int t = queries.size();
        CQueriesDao dao = AppDatabase.getInstance(context).cachedQueries();
        dao.insert(query);
        queries = dao.getAll();
        notifyItemRangeChanged(t, queries.size());
    }

    public void set(List<CachedQuery> list) {
        if (list != null && list.size() > 1) {
            queries = list;
            group.setVisibility(View.VISIBLE);
            notifyItemRangeChanged(0, queries.size());
        } else {
            group.setVisibility(View.GONE);
        }
    }

    public void clear(int pos) {
        notifyItemRemoved(pos);
        AppDatabase.getInstance(context).cachedQueries().delete(queries.get(pos));
        queries.remove(pos);
        if (queries == null || queries.size() == 0) {
            group.setVisibility(View.GONE);
        }
    }

    public void clear() {
        notifyItemRangeRemoved(0, queries.size());
        AppDatabase.getInstance(context).cachedQueries().deleteAll();
        queries.clear();
        group.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return queries.size();
    }

    protected static class CachedQueryVH extends RecyclerView.ViewHolder {
        MaterialCardView root;
        TextView query;
        ImageView image;

        public CachedQueryVH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.adapterCachedQueryRoot);
            query = itemView.findViewById(R.id.adapterCachedQueryText);
            image = itemView.findViewById(R.id.adapterCachedQueryImage);
        }
    }

    public interface CQueryListener {
        void send(String str);
    }
}

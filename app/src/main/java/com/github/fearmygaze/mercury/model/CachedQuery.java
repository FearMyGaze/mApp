package com.github.fearmygaze.mercury.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cachedQueries")
public class CachedQuery {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "query")
    String query;


    public CachedQuery() {
    }

    @Ignore
    public CachedQuery(@NonNull String query) {
        this.query = query;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public void setQuery(@NonNull String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "CachedQueries{" +
                "query='" + query + '\'' +
                '}';
    }
}

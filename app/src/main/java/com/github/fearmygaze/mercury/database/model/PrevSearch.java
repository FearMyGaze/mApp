package com.github.fearmygaze.mercury.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "searches")
public class PrevSearch {

    ///////////////////////////////////////////////////////////////////////////
    // Body
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "byUserId")
    String byUserId;

    @NonNull
    @ColumnInfo(name = "query")
    String query;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public PrevSearch() {
    }

    @Ignore
    public PrevSearch(@NonNull String byUserId, @NonNull String query) {
        this.byUserId = byUserId;
        this.query = query;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    public String getByUserId() {
        return byUserId;
    }

    public void setByUserId(@NonNull String byUserId) {
        this.byUserId = byUserId;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public void setQuery(@NonNull String query) {
        this.query = query;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String toString() {
        return "PrevQueries{" +
                "byUserId='" + byUserId + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}

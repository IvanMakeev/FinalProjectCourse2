package com.example.makeev.myfirstapplication.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "album")
data class Album(
        @Expose
        @SerializedName("id")
        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: Int,
        @Expose
        @SerializedName("name")
        @ColumnInfo(name = "name")
        var name: String,
        @Expose
        @SerializedName("release_date")
        @ColumnInfo(name = "release")
        var releaseDate: String,
        @Expose
        @SerializedName("songs")
        @ColumnInfo(name = "songs")
        @Ignore
        var songs: List<Song>?
) : Serializable {
    constructor() : this(0, "", "", null)
}
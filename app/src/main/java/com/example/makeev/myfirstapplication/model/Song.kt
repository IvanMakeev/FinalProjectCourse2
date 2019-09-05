package com.example.makeev.myfirstapplication.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "song")
data class Song(
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
        @SerializedName("duration")
        @ColumnInfo(name = "duration")
        var duration: String,

        @ColumnInfo(name = "album_id")
        var albumId: Int = 0
)
{
    constructor() : this(0, "", "", 0)
}


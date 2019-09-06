package com.example.makeev.myfirstapplication.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "album_comment")
data class AlbumComment(
        @Expose
        @SerializedName("album_id")
        @ColumnInfo(name = "album_id")
        var albumId: Int,
        @Expose
        @SerializedName("author")
        var author: String,
        @Expose
        @SerializedName("id")
        @PrimaryKey
        var id: Int,
        @Expose
        @SerializedName("text")
        var text: String,
        @Expose
        @SerializedName("timestamp")
        var timestamp: Date?
) : Serializable {
    constructor() : this(0, "", 0, "", null)
}
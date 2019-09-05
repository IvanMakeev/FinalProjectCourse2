package com.example.makeev.myfirstapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Comment(
        @Expose
        @SerializedName("text")
        var text: String,
        @Expose
        @SerializedName("album_id")
        var albumId: String
)
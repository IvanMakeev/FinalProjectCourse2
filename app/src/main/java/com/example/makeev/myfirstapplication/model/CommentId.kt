package com.example.makeev.myfirstapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CommentId(
        @Expose
        @SerializedName("id")
        var id: Int
)
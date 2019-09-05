package com.example.makeev.myfirstapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(@Expose
                @SerializedName("email")
                var login: String,
                @Expose
                @SerializedName("name")
                var name: String,
                @Expose
                @SerializedName("password")
                var password: String
) : Serializable


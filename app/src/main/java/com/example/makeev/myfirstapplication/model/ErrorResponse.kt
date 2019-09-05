package com.example.makeev.myfirstapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ErrorResponse(
        @Expose
        @SerializedName("errors")
        val errors: Errors
) {
    data class Errors(
            @Expose
            @SerializedName("email")
            val email: List<String>?,
            @Expose
            @SerializedName("name")
            val name: List<String>?,
            @Expose
            @SerializedName("password")
            val password: List<String>?
    )
}
package com.example.makeev.myfirstapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data<T>(
        @Expose                         //реализован кастомный GSON для класса User, если убрать @Expose будет вылетать NoSuchElementException
        @SerializedName("data")
        var response: T)


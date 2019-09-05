package com.example.makeev.myfirstapplication

import com.example.makeev.myfirstapplication.model.*
import com.example.makeev.myfirstapplication.model.converter.DataConverterFactory
import com.google.gson.annotations.JsonAdapter
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface AcademyApi {
    @POST("registration")
    fun registration(@Body user: User) : Completable

    //дополнительная аннотация @DataConverterFactory.Json что бы определить что возвращаемое значение не обернуто в Data
    @POST("comments") @DataConverterFactory.Json
    fun postComment(@Body text: Comment) : Single<CommentId>

    @GET("albums")
    fun getAlbums(): Single<List<Album>>

    @GET("albums/{id}")
    fun getAlbum(@Path("id") id: Int): Single<Album>

    @GET("songs")
    fun getSongs(): Call<List<Song>>

    @GET("songs/{id}")
    fun getSong(@Path("id") id: Int): Call<Song>

    @GET("user")
    fun getUser() : Single<User>

    @GET("user")
    fun getUser(@Header("Authorization") credentials: String) : Call<User>

    @GET("comments")
    fun getComments(): Single<List<AlbumComment>>

    @GET("comments/{id}")
    fun getComment(@Path("id") id: Int) : Single<AlbumComment>

    @GET("albums/{id}/comments")
    fun getCommentsForAlbum(@Path("id") id: Int): Single<List<AlbumComment>>
}
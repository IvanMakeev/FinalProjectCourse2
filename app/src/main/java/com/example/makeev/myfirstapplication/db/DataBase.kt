package com.example.makeev.myfirstapplication.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.example.makeev.myfirstapplication.model.Album
import com.example.makeev.myfirstapplication.model.AlbumComment
import com.example.makeev.myfirstapplication.model.Song
import com.example.makeev.myfirstapplication.model.converter.DateConverter


@Database(entities = [Album::class, Song::class, AlbumComment::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao
}
package com.example.makeev.myfirstapplication

import android.app.Application
import android.arch.persistence.room.Room
import com.example.makeev.myfirstapplication.db.DataBase

class App : Application() {

    private var database: DataBase? = null

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(applicationContext, DataBase::class.java, "music_database")
                .fallbackToDestructiveMigration()
                .build()
    }

    fun getDataBase() = database
}

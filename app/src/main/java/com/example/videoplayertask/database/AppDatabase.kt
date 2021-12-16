package com.example.videoplayertask.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videoplayertask.database.daos.VideoDao
import com.example.videoplayertask.database.entities.VideoEntity

@Database(entities = [VideoEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao

    companion object {
        private var appDatabase: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "my_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return appDatabase!!
        }
    }
}
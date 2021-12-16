package com.example.videoplayertask.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.database.daos.BaseDao

@Dao
interface VideoDao : BaseDao<VideoEntity> {

    @Query("select * from videos")
    suspend fun getVideos(): List<VideoEntity>

    @Query("select * from videos where id=:id")
    fun getVideoByID(id: Int): VideoEntity

}
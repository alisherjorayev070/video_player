package com.example.videoplayertask.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.downloader.Status
import java.io.Serializable

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    var id: Int,
    var downloadID: Int,
    var name: String,
    var video_link: String,
    var imageUrl: String,
    var file_path: String,
    var totalBytes: Long,
    var downloadedBytes: Long,
    var status: Status
) : Serializable

package com.example.videoplayertask.utils

import com.example.videoplayertask.models.videodata.VideoData

sealed class VideoResource {
    object Loading : VideoResource()
    data class Success(val list: List<VideoData>?) : VideoResource()
    data class Error(val message: String) : VideoResource()
}

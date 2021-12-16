package com.example.videoplayertask.utils

import com.example.videoplayertask.retrofit.ApiService
import kotlinx.coroutines.flow.flow

class VideoRepository(private val apiService: ApiService) {
    suspend fun getCountries() = flow { emit(apiService.getVideos()) }
}
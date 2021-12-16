package com.example.videoplayertask.retrofit

import com.example.videoplayertask.models.VideosData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("videos/search?query=nature&per_page=20")
    suspend fun getVideos(): Response<VideosData>
}
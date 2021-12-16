package com.example.videoplayertask.models

data class VideosData(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: List<Video>
)
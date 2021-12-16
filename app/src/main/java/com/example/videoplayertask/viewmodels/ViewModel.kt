package com.example.videoplayertask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayertask.models.videodata.VideoData
import com.example.videoplayertask.utils.NetworkHelper
import com.example.videoplayertask.utils.VideoRepository
import com.example.videoplayertask.utils.VideoResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewModel(
    private var videoRepository: VideoRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    fun fetchVideos(): StateFlow<VideoResource> {
        val stateFlow = MutableStateFlow<VideoResource>(VideoResource.Loading)

        viewModelScope.launch {
            val videoList = ArrayList<VideoData>()
            if (networkHelper.isNetworkConnected()) {
                videoRepository.getCountries().catch {
                    stateFlow.emit(VideoResource.Error(it.message ?: ""))
                }.collect {
                    if (it.isSuccessful) {
                        val body = it.body()
                        for (video in body!!.videos) {
                            val videoData = VideoData(
                                video.id,
                                video.duration.toString(),
                                video.video_files[0].link,
                                video.image
                            )

                            videoList.add(videoData)
                        }
                        stateFlow.emit(VideoResource.Success(videoList))
                    } else {
                        when {
                            it.code() in 400..499 -> {
                                stateFlow.emit(VideoResource.Error("Client error"))
                            }
                            it.code() in 500..599 -> {
                                stateFlow.emit(VideoResource.Error("Server error"))
                            }
                            else -> {
                                stateFlow.emit(VideoResource.Error("Other error"))
                            }
                        }
                    }
                }
            } else {
                stateFlow.emit(VideoResource.Error("No internet connection!"))
            }
        }
        return stateFlow
    }
}
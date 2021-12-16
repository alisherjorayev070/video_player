package com.example.videoplayertask.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception

class ViewModelFactory(
    private val videoRepository: VideoRepository,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.example.videoplayertask.viewmodels.ViewModel::class.java)) {
            return com.example.videoplayertask.viewmodels.ViewModel(
                videoRepository,
                networkHelper
            ) as T
        }
        throw Exception("Error")
    }

}
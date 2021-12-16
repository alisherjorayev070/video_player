package com.example.videoplayertask

import android.app.Application
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(applicationContext, config)
    }
}
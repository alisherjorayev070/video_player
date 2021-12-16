package com.example.videoplayertask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.example.videoplayertask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var mediaControls: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (mediaControls == null) {
//            mediaControls = MediaController(this)
//
//            // set the anchor view for the video view
//            mediaControls!!.setAnchorView(binding.videoPlayer)
//
//        }
//
//        binding.videoPlayer.setMediaController(mediaControls)
//        binding.videoPlayer.setVideoPath("https://vod-progressive.akamaized.net/exp=1639423643~acl=%2Fvimeo-prod-skyfire-std-us%2F01%2F1952%2F15%2F384761655%2F1618226604.mp4~hmac=e7be8409ec9d902c5908af3940a7dabf18b615b1c61907da8c8fc1367155e9dc/vimeo-prod-skyfire-std-us/01/1952/15/384761655/1618226604.mp4?filename=video.mp4")
//        binding.videoPlayer.start()

    }
}
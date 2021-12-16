package com.example.videoplayertask.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import com.example.videoplayertask.R
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.databinding.FragmentVideoViewBinding
import com.example.videoplayertask.utils.Utils
import java.lang.Exception

private const val ARG_PARAM1 = "videoEntity"
private const val ARG_PARAM2 = "param2"

class VideoViewFragment : Fragment() {
    private lateinit var binding: FragmentVideoViewBinding
    private var mediaController: MediaController? = null
    private lateinit var dirPath: String
    private var param1: VideoEntity? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as VideoEntity?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoViewBinding.inflate(inflater, container, false)

        dirPath = Utils.getRootDirPath(requireContext()).toString()
        if (mediaController == null) {
            mediaController = MediaController(requireContext())
            mediaController!!.setAnchorView(binding.videoView)
        }

        binding.videoView.setMediaController(mediaController)
        try {
            binding.videoView.setVideoPath(param1!!.file_path + "/." + param1!!.name + ".mp4")
            println(dirPath + param1!!.name + ".mp4")
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
        binding.videoView.start()

        binding.videoView.setOnCompletionListener {
            Toast.makeText(requireContext(), "Video Completed!", Toast.LENGTH_SHORT).show()
        }

        binding.videoView.setOnErrorListener { mediaPlayer, i, i2 ->
            Toast.makeText(
                requireContext(), "An Error Occured While Playing Video !!!", Toast.LENGTH_LONG
            ).show()
            false
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: VideoEntity, param2: String) =
            VideoViewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
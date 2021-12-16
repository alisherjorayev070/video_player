package com.example.videoplayertask.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.videoplayertask.R
import com.example.videoplayertask.adapters.DownloadVideosAdapter
import com.example.videoplayertask.adapters.DownloadVideosAdapter.*
import com.example.videoplayertask.database.AppDatabase
import com.example.videoplayertask.database.daos.VideoDao
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.databinding.FragmentVideoDataBinding
import com.example.videoplayertask.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VideoDataFragment : Fragment(), CoroutineScope {
    private lateinit var binding: FragmentVideoDataBinding
    private lateinit var videoDao: VideoDao
    private lateinit var downloadVideosAdapter: DownloadVideosAdapter
    private lateinit var videoList: ArrayList<VideoEntity>
    private lateinit var dirPath: String
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoDataBinding.inflate(inflater, container, false)

        videoList = ArrayList()
        videoDao = AppDatabase.getInstance(requireContext()).videoDao()
        dirPath = Utils.getRootDirPath(requireContext()).toString()

        lifecycleScope.launch {
            videoList = videoDao.getVideos() as ArrayList<VideoEntity>
            downloadVideosAdapter = DownloadVideosAdapter(videoList, onClickListener)
            binding.rv.adapter = downloadVideosAdapter
        }

        return binding.root
    }

    private val onClickListener = object : OnClickListener {
        override fun onItemClickListener(videoEntity: VideoEntity) {
            val bundle = Bundle()
            println(videoEntity)
            bundle.putSerializable("videoEntity", videoEntity)
            findNavController().navigate(R.id.videoViewFragment, bundle)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main
}
package com.example.videoplayertask.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.downloader.Status
import com.example.videoplayertask.R
import com.example.videoplayertask.adapters.OnlineVideosAdapter
import com.example.videoplayertask.adapters.OnlineVideosAdapter.OnClickListener
import com.example.videoplayertask.database.AppDatabase
import com.example.videoplayertask.database.daos.VideoDao
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.databinding.FragmentOnlineVideosBinding
import com.example.videoplayertask.models.videodata.VideoData
import com.example.videoplayertask.retrofit.ApiClient
import com.example.videoplayertask.retrofit.ApiService
import com.example.videoplayertask.utils.*
import com.example.videoplayertask.viewmodels.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OnlineVideosFragment : Fragment(), CoroutineScope {
    private lateinit var binding: FragmentOnlineVideosBinding
    private lateinit var viewModel: ViewModel
    private lateinit var apiService: ApiService
    private lateinit var onlineVideosAdapter: OnlineVideosAdapter
    private lateinit var videoDao: VideoDao
    private var vidData: VideoEntity? = null
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
        binding = FragmentOnlineVideosBinding.inflate(inflater, container, false)

        apiService = ApiClient.apiService
        videoDao = AppDatabase.getInstance(requireContext()).videoDao()

        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                VideoRepository(apiService),
                NetworkHelper(requireContext())
            )
        )[ViewModel::class.java]

        loadData()
        onlineVideosAdapter = OnlineVideosAdapter(onItemClickListener)
        binding.rv.adapter = onlineVideosAdapter

        return binding.root
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.fetchVideos().collect {
                when (it) {
                    is VideoResource.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is VideoResource.Success -> {
                        onlineVideosAdapter.submitList(it.list)
                    }
                    is VideoResource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val onItemClickListener = object : OnClickListener {
        override fun onDownloadClickListener(videoData: VideoData) {
            vidData = videoDao.getVideoByID(videoData.id)
            if (vidData != null && vidData!!.status == Status.COMPLETED) {
                Toast.makeText(requireContext(), "Video already downloaded", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val videoEntity = VideoEntity(
                    videoData.id,
                    0,
                    videoData.name,
                    videoData.video_link,
                    videoData.imageUrl,
                    "path_name",
                    0,
                    0,
                    Status.UNKNOWN
                )
                videoDao.insert(videoEntity)
                Toast.makeText(requireContext(), "Start downloading", Toast.LENGTH_SHORT).show()
            }
            vidData = null
            findNavController().navigate(R.id.videoDataFragment)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main
}
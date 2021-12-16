package com.example.videoplayertask.adapters

import android.view.LayoutInflater
import android.view.View
import com.example.videoplayertask.R
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downloader.*
import com.example.videoplayertask.database.AppDatabase
import com.example.videoplayertask.database.daos.VideoDao
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.databinding.ItemDownloadVideosBinding
import com.example.videoplayertask.models.videodata.VideoData
import com.example.videoplayertask.utils.Utils
import com.squareup.picasso.Picasso

class DownloadVideosAdapter(var list: ArrayList<VideoEntity>, var listener: OnClickListener) :
    RecyclerView.Adapter<DownloadVideosAdapter.Vh>() {
    private lateinit var dirPath: String
    private lateinit var videoDao: VideoDao
    private var downloadID: Int = 0

    inner class Vh(var itemDownloadVideosBinding: ItemDownloadVideosBinding) :
        RecyclerView.ViewHolder(itemDownloadVideosBinding.root) {

        fun onBind(videoData: VideoEntity, position: Int) {
            itemDownloadVideosBinding.nameTv.text = videoData.name
            Picasso.get().load(videoData.imageUrl).into(itemDownloadVideosBinding.image)

            when (videoData.status) {
                Status.COMPLETED -> {
                    itemDownloadVideosBinding.cancelImg.visibility = View.GONE
                    itemDownloadVideosBinding.playPauseImg.visibility = View.GONE
                    itemDownloadVideosBinding.progressTv.text =
                        Utils.getBytesToMBString(videoData.totalBytes)
                }
                Status.PAUSED -> {
                    itemDownloadVideosBinding.playPauseImg.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                    itemDownloadVideosBinding.progressTv.text =
                        Utils.getProgressDisplayLine(
                            videoData.downloadedBytes,
                            videoData.totalBytes
                        )
                }
                Status.RUNNING -> {
                    itemDownloadVideosBinding.playPauseImg.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                    download(videoData, position)
                }
                Status.UNKNOWN -> {
                    download(videoData, position)
                }
                Status.CANCELLED -> {
                    itemDownloadVideosBinding.cancelImg.visibility = View.GONE
                    itemDownloadVideosBinding.playPauseImg.visibility = View.GONE
                    itemDownloadVideosBinding.progressTv.text = "Cancelled"
                }
                else -> {

                }
            }
        }

        fun download(videoData: VideoEntity, position: Int) {
            downloadID =
                PRDownloader.download(
                    videoData.video_link,
                    dirPath,
                    "." + videoData.name + ".mp4"
                )
                    .build()
                    .setOnProgressListener {
                        val progressPercent: Long = it.currentBytes * 100 / it.totalBytes
                        itemDownloadVideosBinding.progressTv.text =
                            Utils.getProgressDisplayLine(it.currentBytes, it.totalBytes)
                        videoData.downloadedBytes = it.currentBytes
                        videoData.totalBytes = it.totalBytes
                    }
                    .setOnStartOrResumeListener {
                        videoDao.update(videoData)
                    }
                    .setOnPauseListener {
                        itemDownloadVideosBinding.playPauseImg.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                        videoDao.update(videoData)
                    }
                    .setOnCancelListener { }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            videoData.status = Status.COMPLETED
                            videoData.file_path = dirPath
                            videoDao.update(videoData)
                            notifyItemChanged(position)
                        }

                        override fun onError(error: Error?) {

                        }

                    })

            videoData.downloadID = downloadID
            videoDao.update(videoData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        dirPath = Utils.getRootDirPath(parent.context).toString()
        videoDao = AppDatabase.getInstance(parent.context).videoDao()
        return Vh(
            ItemDownloadVideosBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
        holder.itemDownloadVideosBinding.cancelImg.setOnClickListener {
            PRDownloader.cancel(list[position].downloadID)
            videoDao.delete(list[position])
            list.remove(list[position])
            notifyDataSetChanged()
        }
        holder.itemDownloadVideosBinding.playPauseImg.setOnClickListener {
            if (Status.RUNNING == PRDownloader.getStatus(list[position].downloadID)) {
                PRDownloader.pause(list[position].downloadID)
                list[position].status = Status.PAUSED
                videoDao.update(list[position])
                return@setOnClickListener
            }

            if (Status.PAUSED == PRDownloader.getStatus(list[position].downloadID)) {
                PRDownloader.resume(list[position].downloadID)
                list[position].status = Status.RUNNING
                videoDao.update(list[position])
                return@setOnClickListener
            }
            notifyItemChanged(position)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(list[position])
        }

    }

    override fun getItemCount(): Int = list.size

    interface OnClickListener {
        fun onItemClickListener(videoEntity: VideoEntity)
    }
}
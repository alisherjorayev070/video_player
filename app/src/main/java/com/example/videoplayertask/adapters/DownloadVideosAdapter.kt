package com.example.videoplayertask.adapters

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.videoplayertask.R
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.downloader.*
import com.example.videoplayertask.database.AppDatabase
import com.example.videoplayertask.database.daos.VideoDao
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.databinding.ItemDownloadVideosBinding
import com.example.videoplayertask.models.videodata.VideoData
import com.example.videoplayertask.utils.Utils
import com.squareup.picasso.Picasso
import java.lang.Exception

private const val TAG = "DownloadVideosAdapter"

class DownloadVideosAdapter(var list: ArrayList<VideoEntity>, var listener: OnClickListener) :
    RecyclerView.Adapter<DownloadVideosAdapter.Vh>() {
    private lateinit var dirPath: String
    private lateinit var videoDao: VideoDao
    private var downloadID: Int = 0
    private lateinit var notificationManager: NotificationManager

    inner class Vh(var itemDownloadVideosBinding: ItemDownloadVideosBinding) :
        RecyclerView.ViewHolder(itemDownloadVideosBinding.root) {

        fun onBind(videoData: VideoEntity, position: Int) {
            itemDownloadVideosBinding.nameTv.text = videoData.name
            try {
                Picasso.get().load(videoData.imageUrl).into(itemDownloadVideosBinding.image)
            } catch (e: Exception) {

            }

            Log.d(
                TAG, "onBind: ${
                    MimeTypeMap.getFileExtensionFromUrl(videoData.video_link)
                }\n${MimeTypeMap.getFileExtensionFromUrl(videoData.imageUrl)}"
            )

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
                    //Utils.setNotification(itemView.context, videoData)
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
                        //Utils.setNotification(itemView.context, videoData)
                    }
                    .setOnStartOrResumeListener {
                        itemDownloadVideosBinding.playPauseImg.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                        videoData.status = Status.RUNNING
                        videoDao.update(videoData)
                        Utils.setNotification(itemView.context, list[position])
                    }
                    .setOnPauseListener {
                        itemDownloadVideosBinding.playPauseImg.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                        videoData.status = Status.PAUSED
                        videoDao.update(videoData)
                        Utils.setNotification(itemView.context, list[position])
                    }
                    .setOnCancelListener {
                        list.remove(videoData)
                        notifyDataSetChanged()
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            videoData.status = Status.COMPLETED
                            videoData.file_path = dirPath
                            videoDao.update(videoData)
                            notifyItemChanged(position)
                            notificationManager.cancel(videoData.id)
                        }

                        override fun onError(error: Error?) {
                            Toast.makeText(
                                itemView.context,
                                "Error in downloading",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })

            videoData.downloadID = downloadID
            videoDao.update(videoData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        dirPath = Utils.getRootDirPath(parent.context).toString()
        videoDao = AppDatabase.getInstance(parent.context).videoDao()
        notificationManager =
            parent.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            notificationManager.cancel(list[position].id)
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
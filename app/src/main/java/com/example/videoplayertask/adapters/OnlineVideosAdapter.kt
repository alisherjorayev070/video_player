package com.example.videoplayertask.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayertask.databinding.ItemOnlineVideosBinding
import com.example.videoplayertask.models.videodata.VideoData
import com.squareup.picasso.Picasso

class OnlineVideosAdapter(var listener: OnClickListener) :
    ListAdapter<VideoData, OnlineVideosAdapter.Vh>(MyDiffUtill()) {

    class MyDiffUtill : DiffUtil.ItemCallback<VideoData>() {
        override fun areItemsTheSame(
            oldItem: VideoData,
            newItem: VideoData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: VideoData,
            newItem: VideoData
        ): Boolean {
            return oldItem == newItem
        }

    }

    inner class Vh(var itemOnlineVideosBinding: ItemOnlineVideosBinding) :
        RecyclerView.ViewHolder(itemOnlineVideosBinding.root) {

        @SuppressLint("ResourceType")
        fun onBind(videoData: VideoData) {
            itemOnlineVideosBinding.nameTv.text = videoData.id.toString()
            Picasso.get().load(videoData.imageUrl).into(itemOnlineVideosBinding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemOnlineVideosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
        holder.itemOnlineVideosBinding.downloadImg.setOnClickListener {
            listener.onDownloadClickListener(getItem(position))
        }
    }

    interface OnClickListener {
        fun onDownloadClickListener(videoData: VideoData)
    }
}
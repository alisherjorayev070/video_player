package com.example.videoplayertask.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.videoplayertask.MainActivity
import com.example.videoplayertask.R
import com.example.videoplayertask.database.AppDatabase
import com.example.videoplayertask.database.entities.VideoEntity
import com.example.videoplayertask.models.Actions
import java.io.File
import java.io.Serializable
import java.util.*

private const val TAG = "Utils"

class Utils {

    companion object {
        fun getRootDirPath(context: Context): String? {
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                val file: File = ContextCompat.getExternalFilesDirs(
                    context.applicationContext,
                    null
                )[0]
                file.absolutePath
            } else {
                context.applicationContext.filesDir.absolutePath
            }
        }

        fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String? {
            return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
        }

        fun getBytesToMBString(bytes: Long): String {
            return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
        }

        @SuppressLint("RemoteViewLayout")
        fun setNotification(context: Context, videoEntity: VideoEntity) {
            val channelID = "com.example.videoplayertask"

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val contentView = RemoteViews(context.packageName, R.layout.custom_notification_view)

            contentView.setTextViewText(
                R.id.progress_tv,
                videoEntity.name
            )

            if (videoEntity.status == Status.PAUSED)
                contentView.setImageViewResource(
                    R.id.play_pause_not_img,
                    R.drawable.ic_baseline_play_circle_filled_24
                )
            else {
                contentView.setImageViewResource(
                    R.id.play_pause_not_img,
                    R.drawable.ic_baseline_pause_circle_filled_24
                )
            }

            val builder = NotificationCompat.Builder(context.applicationContext, channelID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCustomContentView(contentView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelID,
                    "Custom Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
                builder.setChannelId(channelID)
            }

            val notification = builder.build()

            val notificationIntent = Intent(context, MainActivity::class.java)
            val contentIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0)
            builder.setContent(contentView)

            Log.d(TAG, "setNotification: $videoEntity")

            notificationIntent.putExtra("videoEntity", videoEntity)
            notification.contentIntent = contentIntent

            val playIntent = Intent(context, NotificationListener::class.java)
            playIntent.putExtra("videoEntity", videoEntity)
            playIntent.action = Actions.ACTION_PLAY
            val pendingPlayIntent =
                PendingIntent.getBroadcast(context, videoEntity.id + 102, playIntent, 0)
            val cancelIntent = Intent(context, NotificationListener::class.java)
            cancelIntent.putExtra("videoEntity", videoEntity)
            cancelIntent.action = Actions.ACTION_CANCEL
            val pendingCancelIntent =
                PendingIntent.getBroadcast(context, videoEntity.id + 103, cancelIntent, 0)
            contentView.setOnClickPendingIntent(R.id.play_pause_not_img, pendingPlayIntent)
            contentView.setOnClickPendingIntent(R.id.cancel_not_img, pendingCancelIntent)

            notificationManager.notify(videoEntity.id, notification)

        }
    }

}
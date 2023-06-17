package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Applicationclass:Application() {
    companion object{
        const val channel_id = "channel1"
        const val play = "play"
        const val Next = "next"
        const val previous = "previous"
        const val exit = "exit"
    }
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            val notificationchannel = NotificationChannel(channel_id,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            notificationchannel.description ="This is a important channel for showing song "
            val notificationManager = getSystemService(NOTIFICATION_SERVICE)as NotificationManager
            notificationManager.createNotificationChannel(notificationchannel)


        }
    }
}
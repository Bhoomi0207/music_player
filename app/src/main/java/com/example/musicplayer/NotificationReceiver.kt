package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context:Context?, intent:Intent?) {
        when (intent?.action) {
            Applicationclass.previous->Toast.makeText(
                context,
                "Previous clicked",
                Toast.LENGTH_SHORT
            )
            Applicationclass.play->Toast.makeText(context, "Play clicked", Toast.LENGTH_SHORT)
            Applicationclass.Next->Toast.makeText(context, "next clicked", Toast.LENGTH_SHORT)
            Applicationclass.exit-> {
                Player_Activity.musicService!!.stopForeground(true)
                Player_Activity.musicService!!.mediaPlayer!!.release()
                Player_Activity.musicService = null
                exitProcess(1)
            }
        }
    }


}
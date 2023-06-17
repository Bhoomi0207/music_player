package com.example.musicplayer
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat


class MusicService:Service() {
    private  var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer?= null
    private lateinit var runnable:Runnable
    lateinit var audioManager : AudioManager
    private lateinit var mediaSession : MediaSessionCompat

    override fun onBind(intent:Intent?):IBinder {
        mediaSession = MediaSessionCompat(baseContext,"MY MUSIC")
     return  myBinder
    }
    inner class MyBinder :Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }
    fun showNotification(playPauseBtn:Int){
     val intent = Intent(baseContext, MainActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(Applicationclass.previous)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(Applicationclass.play)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(Applicationclass.Next)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(Applicationclass.exit)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)


        val notification =  androidx.core.app.NotificationCompat.Builder(baseContext,Applicationclass.channel_id)
            .setContentIntent(contentIntent)
            .setContentTitle(Player_Activity.musicListPA[Player_Activity.songPosition].title)
            .setContentText(Player_Activity.musicListPA[Player_Activity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_playlist)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.music_splash_screen))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority( androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility( androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous_icon,"previous",prevPendingIntent)

            .addAction(R.drawable.ic_play,"play",playPendingIntent)
            .addAction(R.drawable.ic_next_icon,"next",nextPendingIntent)
            .addAction(R.drawable.ic_exit,"exit",exitPendingIntent)
            .build()
        startForeground(13,notification)

    }
    fun createMediaPlayer(){
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(Player_Activity.musicListPA[Player_Activity.songPosition].path)
            mediaPlayer!!.prepare()
            Player_Activity.binding.playPauseBtn.setIconResource(R.drawable.ic_play_pause)
            showNotification(R.drawable.ic_play_pause)
            Player_Activity.binding.seekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player_Activity.binding.seekbarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            Player_Activity.binding.seekbarPA.progress = 0
            Player_Activity.binding.seekbarPA.max = mediaPlayer!!.duration
            Player_Activity.nowPlayingId = Player_Activity.musicListPA[Player_Activity.songPosition].id
            Player_Activity.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
            Player_Activity.loudnessEnhancer.enabled = true
        }catch (e: Exception){return}
    }

    fun seekBarSetup(){
        runnable = Runnable {
            Player_Activity.binding.seekbarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player_Activity.binding.seekbarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    }

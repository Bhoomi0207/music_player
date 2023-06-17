package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Player_Activity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding:ActivityPlayerBinding

        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int = 0
        lateinit var loudnessEnhancer:LoudnessEnhancer
        var isPlaying:Boolean = false
        var nowPlayingId:String = ""
        var musicService:MusicService? = null
        var repeat:Boolean=false
        var min15:Boolean=false
        var min30:Boolean=false
        var min60:Boolean=false
        var nowPlaying:String = ""
        var isFavourite:Boolean= false
        var fIndex :Int = -1


    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializelayout()
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()

        }
        binding.prevBtn.setOnClickListener {
            prev_next(increment = false)
        }
        binding.nextBtn.setOnClickListener {
            prev_next(increment = true)
        }
        binding.seekbarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar:SeekBar?, progress:Int, fromUser:Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)

            }

            override fun onStartTrackingTouch(seekBar:SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar:SeekBar?) = Unit
        })
        binding.repeatbtnPA.setOnClickListener{
            if(!repeat){
                repeat = true
                binding.repeatbtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }else{
                repeat = false
                binding.repeatbtnPA.setColorFilter(ContextCompat.getColor(this,R.color.orange))
            }
        }
        binding.equlizerbtnPA.setOnClickListener {
           try {
               val EQIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               EQIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
               EQIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
               EQIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(EQIntent,13)
           }catch (e:Exception){
               Toast.makeText(this,"Equilizer feature not supported",Toast.LENGTH_SHORT).show()
           }
        }
        binding.timerbtnPA.setOnClickListener {
            val timer = min15|| min30|| min60
            if(!timer) showBottomSheetDialogue()
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("stop timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes"){ _, _ ->
                        min15=false
                        min30=false
                        min60=false
                        binding.timerbtnPA.setColorFilter(ContextCompat.getColor(this,R.color.orange))

                    }
                    .setNegativeButton("No"){ dialog , _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }
        }
        binding.sharebtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action=Intent.ACTION_SEND
            shareIntent.type="audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"sharing music file!!"))
        }
        binding.favouritebtnPA.setOnClickListener {
            if(isFavourite){
                isFavourite=false
                binding.favouritebtnPA.setImageResource(R.drawable.ic_favorite_border)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            }
            else{

                    isFavourite=true
                    binding.favouritebtnPA.setImageResource(R.drawable.ic_favorite)
                    FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
                }

        }
    }

    private fun setLayout() {
        fIndex= favouitechecker(musicListPA[songPosition].id)
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
        if (repeat) binding.repeatbtnPA.setColorFilter(ContextCompat.getColor(applicationContext,
            R.color.black
        ))
        if(min15 || min30 || min60)  binding.timerbtnPA.setColorFilter(ContextCompat.getColor(applicationContext,
            R.color.black
        ))
        if (isFavourite) binding.favouritebtnPA.setImageResource(R.drawable.ic_favorite)
        else binding.favouritebtnPA.setImageResource((R.drawable.ic_favorite_border))
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.ic_play_pause)
            binding.seekbarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress = 0
            binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener (this)
            nowPlayingId = musicListPA[songPosition].id

        } catch (e:Exception) {
            return
        }


    }

    private fun initializelayout() {

        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "FavouriteAdapter"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                setLayout()
            }
            "Nowplaying" -> {
                setLayout()
                binding.seekbarStart.text = formatDuration( musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekbarEnd.text = formatDuration( musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbarPA.progress =  musicService!!.mediaPlayer!!.currentPosition
                binding.seekbarPA.max=musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.playPauseBtn.setIconResource(R.drawable.ic_play_pause)
                else binding.playPauseBtn.setIconResource(R.drawable.ic_play)
            }
            "MusicAdapterSearch"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.musiclistsearch)
                setLayout()
            }
            "MusicAdapter"-> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity"-> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMediaPlayer()
            }
            "PlaylistAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(Playlist_Activity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
        }
    }

    private fun playMusic() {
        isPlaying = true
        binding.playPauseBtn.setIconResource(R.drawable.ic_play_pause)
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        isPlaying = false
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prev_next(increment:Boolean) {
        if (increment) {
            setsongPositon(increment = true)
            setLayout()
            createMediaPlayer()

        } else {
            setsongPositon(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    private fun setsongPositon(increment:Boolean) {
        if(!Player_Activity.repeat) {
            if (increment) {
                if (musicListPA.size - 1 == songPosition)
                    songPosition = 0
                else ++songPosition
            } else {
                if (0 == songPosition)
                    songPosition = musicListPA.size - 1
                else --songPosition
            }
        }
    }

    override fun onServiceConnected(name:ComponentName?, service:IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
      //  musicService!!.showNotification()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name:ComponentName? ) {
        musicService = null
    }

    override fun onCompletion(mp:MediaPlayer?) {
       setsongPositon(true)
        createMediaPlayer()
        try { setLayout()} catch (e:Exception){return}
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==13 || resultCode== RESULT_OK)
            return
    }

    private  fun showBottomSheetDialogue(){
        val dialogue = BottomSheetDialog(this)
        dialogue.setContentView(R.layout.bottom_sheet_dialogue)
        dialogue.show()
        dialogue.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerbtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            min15 = true
            Thread {
                Thread.sleep((15*60000).toLong())
                if(min15 ) exitApplication() }.start()
            dialogue.dismiss()
        }
        dialogue.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext,"Music will stop after 30 minutes",Toast.LENGTH_SHORT).show()
            binding.timerbtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            min30 = true
            Thread {
                Thread.sleep((30*60000).toLong())
                    if(min30 ) exitApplication() }.start()
            dialogue.dismiss()
        }
        dialogue.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext,"Music will stop after 60 minutes",Toast.LENGTH_SHORT).show()
            binding.timerbtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            min60 = true
            Thread {
                Thread.sleep((60*60000).toLong())
                if(min60 ) exitApplication() }.start()
            dialogue.dismiss()
        }
    }
}


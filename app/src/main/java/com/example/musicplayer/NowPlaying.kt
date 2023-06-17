package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding:FragmentNowPlayingBinding


    }

    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View? {
        val view= inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding= FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playpauseBtnNP.setOnClickListener {
            if(Player_Activity.isPlaying) pausemusic() else playmusic()
        }
     binding.nextBtnNP.setOnClickListener {
         setSongPosition(increment = true)
         Player_Activity.musicService!!.createMediaPlayer()
         Glide.with(requireContext())
             .load(Player_Activity.musicListPA[Player_Activity.songPosition].artUri)
             .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
             .into(binding.songImgNP)
         binding.songnameNP.text = Player_Activity.musicListPA[Player_Activity.songPosition].title
         Player_Activity.musicService!!.showNotification(R.drawable.ic_play_pause)
         playMusic()
     }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(),Player_Activity::class.java)
            intent.putExtra("index",Player_Activity.songPosition)
            intent.putExtra("class","Nowplaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (Player_Activity.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songnameNP.isSelected = true
            Glide.with(requireContext())
                .load(Player_Activity.musicListPA[Player_Activity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
                .into(binding.songImgNP)
            binding.songnameNP.text = Player_Activity.musicListPA[Player_Activity.songPosition].title
            if(Player_Activity.isPlaying) binding.playpauseBtnNP.setIconResource(R.drawable.ic_play_pause)
            else binding.playpauseBtnNP.setIconResource(R.drawable.ic_play)
        }
    }
    private fun playmusic(){
        Player_Activity.musicService!!.mediaPlayer!!.start()
        binding.playpauseBtnNP.setIconResource(R.drawable.ic_play_pause)
        Player_Activity.musicService!!.showNotification(R.drawable.ic_play_pause)
        Player_Activity.binding.nextBtn.setIconResource(R.drawable.ic_play_pause)
        Player_Activity.isPlaying=true

    }
    private fun pausemusic(){
        Player_Activity.musicService!!.mediaPlayer!!.pause()
        binding.playpauseBtnNP.setIconResource(R.drawable.ic_play)
        Player_Activity.musicService!!.showNotification(R.drawable.ic_play)
        Player_Activity.binding.nextBtn.setIconResource(R.drawable.ic_play)
        Player_Activity.isPlaying= false
    }

    private fun playMusic(){
        Player_Activity.isPlaying = true
        Player_Activity.musicService!!.mediaPlayer!!.start()
        binding.playpauseBtnNP.setIconResource(R.drawable.ic_play_pause)
        Player_Activity.musicService!!.showNotification(R.drawable.ic_play_pause)
    }


}


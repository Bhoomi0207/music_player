package com.example.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogueBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class Playlist_Activity : AppCompatActivity() {
    private lateinit var binding:ActivityPlaylistBinding
    private lateinit var adapter : PlaylistviewAdapter

    companion object {
        var musicPlaylist:MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)

        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
           val tempList = ArrayList<String>()
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@Playlist_Activity,2)
        adapter = PlaylistviewAdapter(this, playList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter


        binding.backBtnPlaylist.setOnClickListener {
            finish()
        }
        binding.playlistbtn.setOnClickListener {
            customAlertDialog()
        }

    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@Playlist_Activity).inflate(R.layout.add_playlist_dialogue,binding.root,false)
        val binder= AddPlaylistDialogueBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){ dialog , _ ->
                 val playListName = binder.playlistName.text
                  val createdBy = binder.yourName.text
                if(playListName!=null && createdBy!=null)
                    if (playListName.isNotEmpty()&& createdBy.isNotEmpty()){
                        addPlaylist(playListName.toString(),createdBy.toString())
                    }
                dialog.dismiss()
            }
            .setNegativeButton("No"){ dialog , _ ->
                dialog.dismiss()
            }.show()

    }
    private fun addPlaylist(name:String,createdBy:String){
        var playlistEXits = false
        for (i in musicPlaylist.ref){
            if (name.equals(i.name)){
                playlistEXits = true
                break
            }
        }
        if (playlistEXits) Toast.makeText(this,"PlayList Exist",Toast.LENGTH_SHORT).show()
        else{
            val tempPlayList = Playlist()
            tempPlayList.name = name
            tempPlayList.playlist = ArrayList()
            tempPlayList.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlayList.createdOn  = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlayList)
            adapter.refreshPlaylist()
        }
    }
}
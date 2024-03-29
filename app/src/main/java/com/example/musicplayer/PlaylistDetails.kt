package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding : ActivityPlaylistDetailsBinding
    lateinit var adapter:MusicAdapter

    companion object{
        var currentPlaylistPos :Int = -1
    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.get("index") as Int

       /* try{Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].playlist =
            checkPlaylist(playlist = Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].playlist)}
        catch(e: Exception){}*/
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].playlist.addAll(MainActivity.MusicListMA)
        adapter = MusicAdapter(this, Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        binding.shufflebtnPD.setOnClickListener {
            val intent = Intent(this, Player_Activity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
       /* binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }*/
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(Playlist_Activity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
                .into(binding.playlistImgPD)
            binding.shufflebtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(Playlist_Activity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }
}
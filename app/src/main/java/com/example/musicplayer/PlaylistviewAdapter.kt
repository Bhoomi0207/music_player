package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.PlaylistviewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistviewAdapter(private val context:Context, private var playList:ArrayList<Playlist>): RecyclerView.Adapter<PlaylistviewAdapter.ViewHolder> () {



    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ViewHolder {
        return ViewHolder(PlaylistviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    class ViewHolder(binding:PlaylistviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        var delete = binding.playlistDeleteBtn

    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int) {
        holder.name.text =playList[position].name
        holder.name.isSelected=true
     /*   Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
            .into(holder.image)*/
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to delete playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    Playlist_Activity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                 dialog.dismiss()

                }
                .setNegativeButton("No"){ dialog , _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

        }
        holder.root.setOnClickListener{
         val intent = Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if(Playlist_Activity.musicPlaylist.ref[position].playlist.size >0){
            Glide.with(context)
                .load(Playlist_Activity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
                .into(holder.image)
        }


    }


    override fun getItemCount():Int {
        return playList.size
    }
    fun refreshPlaylist(){
        playList = ArrayList()
        playList.addAll(Playlist_Activity.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}




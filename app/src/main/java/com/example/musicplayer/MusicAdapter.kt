package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicviewBinding



class MusicAdapter(private val context:Context,private var musicList:ArrayList<Music>,private var playlistDetails:Boolean = false): RecyclerView.Adapter<MusicAdapter.ViewHolder> () {


    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int) : MusicAdapter.ViewHolder {
        return ViewHolder(MusicviewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder:MusicAdapter.ViewHolder, position:Int) {
        holder.title.text=musicList[position].title
        holder.album.text=musicList[position].album
        holder.duration.text= formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
            .into(holder.image)
        when{
            playlistDetails ->{
                holder.root.setOnClickListener {
                    sendIntents(ref = "PlaylistAdapter", pos = position)

                }

            }
            else ->{
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search->sendIntents(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == Player_Activity.nowPlayingId->sendIntents(
                            ref = "Nowplaying",
                            pos = Player_Activity.songPosition
                        )
                        else->sendIntents(ref = "MusicAdapter", pos = position)


                    }
                }
            }

        }
    }


    override fun getItemCount():Int {
     return musicList.size
    }

    class ViewHolder(binding:MusicviewBinding):RecyclerView.ViewHolder(binding.root) {
        val title= binding.songName
        val album = binding.songAlbum
        val image = binding.imageMV
        val duration=binding.songDuration
        val root = binding.root

    }
    fun updateMusicList(searchList:ArrayList<Music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntents(ref:String,pos:Int){
        val intent = Intent(context,Player_Activity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }

}

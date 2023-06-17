package com.example.musicplayer

import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String,
                  val title:String,
                  val album:String,
                  val artist:String,
                  val path :String,
                  val duration:Long=0,val artUri :String)

class Playlist{
    lateinit var name : String
    lateinit var playlist : ArrayList<Music>
    lateinit var createdBy :String
    lateinit var createdOn : String
   }
   class MusicPlaylist{
       var ref:ArrayList<Playlist> = ArrayList()


     }


   fun formatDuration(duration:Long):String {
     val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
     val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS) -
             minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
     return String.format("%02d:%02d",minutes,seconds)

 }
fun exitApplication(){
    if(Player_Activity.musicService !=null){
        Player_Activity.musicService!!.stopForeground(true)
        Player_Activity.musicService!!.mediaPlayer!!.release()
        Player_Activity.musicService=null}
    exitProcess(1)
}

fun setSongPosition(increment: Boolean) {
    if (!Player_Activity.repeat) {
        if (increment) {
            if (Player_Activity.musicListPA.size - 1 == Player_Activity.songPosition)
                Player_Activity.songPosition = 0
            else ++Player_Activity.songPosition
        } else {
            if (0 == Player_Activity.songPosition)
                Player_Activity.songPosition = Player_Activity.musicListPA.size - 1
            else --Player_Activity.songPosition
        }
    }
}

    fun favouitechecker(id:String):Int{
        Player_Activity.isFavourite=false
        FavouriteActivity.favouriteSongs.forEachIndexed{index,music ->
            if (id==music.id){
                Player_Activity.isFavourite = true
                return index
            }

        }
        return  -1

    }



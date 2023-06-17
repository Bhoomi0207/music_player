package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FavouriteviewBinding

class FavouriteAdapter(private val context:Context, private var musicList:ArrayList<Music>): RecyclerView.Adapter<FavouriteAdapter.ViewHolder> () {



    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ViewHolder {
        return ViewHolder(FavouriteviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    class ViewHolder(binding:FavouriteviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root

    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
            .into(holder.image)

         //when play next music is clicked
      //  if(playNext){
            holder.root.setOnClickListener {
                val intent = Intent(context, Player_Activity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
          /*  holder.root.setOnLongClickListener {
                val customDialog = LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                bindingMF.AddToPNBtn.text = "Remove"
                bindingMF.AddToPNBtn.setOnClickListener {
                    if(position == Player_Activity.songPosition)
                        Snackbar.make((context as Activity).findViewById(R.id.linearLayoutPN),
                            "Can't Remove Currently Playing Song.", Snackbar.LENGTH_SHORT).show()
                    else{
                        if(Player_Activity.songPosition < position && Player_Activity.songPosition != 0) --Player_Activity.songPosition
                        PlayNext.playNextList.removeAt(position)
                        Player_Activity.musicListPA.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }
        }else{
            holder.root.setOnClickListener {
                val intent = Intent(context, Player_Activity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }*/
    }


    override fun getItemCount():Int {
        return musicList.size
    }
}



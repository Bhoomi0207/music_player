package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var musicAdapter:MusicAdapter

    companion object {
        lateinit var MusicListMA:ArrayList<Music>
        lateinit var musiclistsearch:ArrayList<Music>
        var search:Boolean=false
    }


    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestsRumTimePermission()) {
            initializedLayout()
            // for retrieving favourites data
            FavouriteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("Favourites", MODE_PRIVATE)
            val jsonString= editor.getString("Favourite songs",null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            val data:ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
            if(jsonString!=null){
                val data : ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavouriteActivity.favouriteSongs.addAll(data)
            }


        }

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this, Player_Activity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }
        binding.favouritesBtn.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistsBtn.setOnClickListener {
            val intent = Intent(this, Playlist_Activity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback->Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.navSettings->Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.navAbout->Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit->{
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("EXIT")
                        .setMessage("Do you want to close app?")
                        .setPositiveButton("Yes"){ _, _ ->
                           exitApplication()

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
            true
        }

    }


    // for requesting permission
    private fun requestsRumTimePermission():Boolean {
        if (ActivityCompat.checkSelfPermission
                (
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode:Int,
        permissions:Array<out String>,
        grantResults:IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
               initializedLayout()

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)

    }

    @SuppressLint("SetTextI18n")
    private fun initializedLayout() {
        search=false
        MusicListMA = getAllAudio()
        binding.musicRv.setHasFixedSize(true)
        binding.musicRv.setItemViewCacheSize(13)
        binding.musicRv.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicListMA)
        binding.musicRv.adapter = musicAdapter
        binding.totalSong.text = "Total Songs" + musicAdapter.itemCount
    }

    @SuppressLint("Range")
    private fun getAllAudio():ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID

        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIDC= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri =Uri.parse("content://media/external/audio/albumart")
                    val artUriC =Uri.withAppendedPath(uri,albumIDC).toString()

                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path=  pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList


    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        if(!Player_Activity.isPlaying && Player_Activity.musicService!=null){
            Player_Activity.musicService!!.stopForeground(true)
            Player_Activity.musicService!!.mediaPlayer!!.release()
            Player_Activity.musicService=null
            exitProcess(1)

        }

    }

    override fun onResume() {
        super.onResume()
        // for storing favourites data
        val editor = getSharedPreferences("Favourites", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("Favourite songs",jsonString)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu:Menu?):Boolean {
        menuInflater.inflate(R.menu.search_view_menu,menu)
        val searchview = menu?.findItem(R.id.searchview)?.actionView as androidx.appcompat.widget.SearchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String?):Boolean = true
            override fun onQueryTextChange(newText:String?):Boolean {
                musiclistsearch= ArrayList()
             if (newText!=null){
                 val userInput=newText.lowercase()
                 for (song in MusicListMA)
                     if (song.title.lowercase().contains(userInput))
                         musiclistsearch.add(song)
                 search = true
                 musicAdapter.updateMusicList(searchList = musiclistsearch)
             }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
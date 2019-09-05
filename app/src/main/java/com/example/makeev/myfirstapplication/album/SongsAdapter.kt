package com.example.makeev.myfirstapplication.album

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.Song
import java.util.ArrayList

class SongsAdapter : RecyclerView.Adapter<SongsHolder>() {

    private val songs = ArrayList<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_song, parent, false)
        return SongsHolder(view)
    }

    override fun onBindViewHolder(holder: SongsHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun addData(data: List<Song>, isRefreshed: Boolean) {
        if (isRefreshed) {
            songs.clear()
        }
        songs.addAll(data)
        notifyDataSetChanged()
    }
}
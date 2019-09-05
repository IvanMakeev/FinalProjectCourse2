package com.example.makeev.myfirstapplication.album

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.Song

class SongsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.tv_title_song)
    private val duration: TextView = itemView.findViewById(R.id.tv_duration_song)

    fun bind(item: Song) {
        title.text = item.name
        duration.text = item.duration
    }
}
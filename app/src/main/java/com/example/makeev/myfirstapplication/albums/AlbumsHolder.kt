package com.example.makeev.myfirstapplication.albums

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.Album

class AlbumsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.tv_title)
    var releaseData: TextView = itemView.findViewById(R.id.tv_release_date)

    fun bind(item: Album, onItemClickListener: AlbumsAdapter.OnItemClickListener) {
        title.text = item.name
        releaseData.text = item.releaseDate
        itemView.setOnClickListener { onItemClickListener.onItemClick(item) }
    }
}
package com.example.makeev.myfirstapplication.albums

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.Album
import java.util.ArrayList


class AlbumsAdapter(private val mOnClickListener: OnItemClickListener) : RecyclerView.Adapter<AlbumsHolder>() {

    private val albums = ArrayList<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_album, parent, false)
        return AlbumsHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumsHolder, position: Int) {
        val album = albums[position]
        holder.bind(album, mOnClickListener)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    fun addData(data: List<Album>, isRefresher: Boolean) {
        if (isRefresher)
            albums.clear()

        albums.addAll(data)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(album: Album)
    }
}
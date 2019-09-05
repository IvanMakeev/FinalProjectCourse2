package com.example.makeev.myfirstapplication.comment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.AlbumComment
import java.util.*

class CommentsAdapter : RecyclerView.Adapter<CommentsHolder>() {

    private val comments = ArrayList<AlbumComment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_comment, parent, false)
        return CommentsHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentsHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    fun addData(data: List<AlbumComment>, isRefresher: Boolean, isNewComment: Boolean = false) {
        if (isRefresher) {
            comments.clear()
        }

        if (!isNewComment) {
            comments.addAll(data)
            comments.reverse()
        } else if (data.isNotEmpty()) {
            comments.add(0, data[0])
        }

        notifyDataSetChanged()
    }
}
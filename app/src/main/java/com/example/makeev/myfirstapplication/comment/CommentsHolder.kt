package com.example.makeev.myfirstapplication.comment

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.makeev.myfirstapplication.ApiUtils
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.model.AlbumComment

class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val authorComment: TextView = itemView.findViewById(R.id.tv_author_comment)
    private val textComment: TextView = itemView.findViewById(R.id.tv_text_comment)
    private val dateComment: TextView = itemView.findViewById(R.id.tv_date_comment)

    fun bind(item: AlbumComment){
        authorComment.text = item.author
        textComment.text = item.text
        dateComment.text = ApiUtils.convertTimestamp(item.timestamp!!)
    }

}

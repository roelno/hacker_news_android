package com.zelda.hackernewsandroid.ui.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zelda.hackernewsandroid.Items
import com.zelda.hackernewsandroid.R

class CommentsAdapter(private val comments: MutableList<Items>) :
    RecyclerView.Adapter<CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentPoster.text = comment.by
        holder.commentTextView.text = comment.text
    }

    override fun getItemCount() = comments.size

    fun updateComments(newComments: List<Items>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}


class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val commentPoster: TextView = view.findViewById(R.id.user_name_text)
    val commentTextView: TextView = view.findViewById(R.id.comment_text)
}
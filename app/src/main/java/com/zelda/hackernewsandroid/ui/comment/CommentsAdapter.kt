package com.zelda.hackernewsandroid.ui.comment

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
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
        val userName = comment.by ?: ""
        val timeAgo = getTimeAgo(comment.time!!)

        val styledText = SpannableString("$userName  $timeAgo").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.rgb(255,102, 0)), 0, userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val startIndexOfTime = userName.length + 2 // Including the comma and space
            setSpan(StyleSpan(Typeface.ITALIC), startIndexOfTime, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.LTGRAY), startIndexOfTime, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        holder.commentPoster.text = styledText
        holder.commentTextView.text = comment.text

        // Set visibility of the expand button
        holder.expandButton.visibility = if (comment.childComments?.isNotEmpty() == true) View.VISIBLE else View.GONE

        // Set click listener for expand/collapse
        holder.expandButton.setOnClickListener {
            // Toggle the visibility of child comments

        }

    }


    override fun getItemCount() = comments.size

    fun updateComments(newComments: List<Items>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    fun getTimeAgo(time: Long): String {
        val currentTime = System.currentTimeMillis() / 1000
        val diffInSeconds = currentTime - time

        val minutes = diffInSeconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }
}


class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val commentPoster: TextView = view.findViewById(R.id.user_name_text)
    val commentTextView: TextView = view.findViewById(R.id.comment_text)
    val expandButton: ToggleButton = view.findViewById(R.id.expand_button)
}
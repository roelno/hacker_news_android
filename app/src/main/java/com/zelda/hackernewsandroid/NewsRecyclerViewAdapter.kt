package com.zelda.hackernewsandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsRecyclerViewAdapter(
    private val newsList: MutableList<Items>,
    private val onClick: (Items) -> Unit
) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val newsItem = layoutInflater.inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(newsItem)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.bind(newsItem)
        holder.itemView.setOnClickListener { onClick(newsItem) }
    }

    fun updateNewsList(news: List<Items>) {
        newsList.clear()
        newsList.addAll(news)
        notifyDataSetChanged()
    }

}

class NewsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(news: Items) {
        val title = view.findViewById<TextView>(R.id.title_text)
        val content = view.findViewById<TextView>(R.id.content_text)
        title.text = news.title
        content.text = news.context
    }
}
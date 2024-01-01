package com.zelda.hackernewsandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsRecyclerViewAdapter(val newsList: MutableList<News>) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val newsItem = layoutInflater.inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(newsItem)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
    }

    fun updateNewsList(news: List<News>) {
        newsList.clear()
        newsList.addAll(news)
        notifyDataSetChanged()
    }

}

class NewsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(news: News) {
        val title = view.findViewById<TextView>(R.id.title_text)
        val content = view.findViewById<TextView>(R.id.content_text)
        title.text = news.title
        content.text = news.context
    }
}
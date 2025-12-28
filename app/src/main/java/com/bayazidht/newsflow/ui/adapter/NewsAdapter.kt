package com.bayazidht.newsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsArticle
import com.bumptech.glide.Glide
import com.bayazidht.newsflow.databinding.ItemNewsCardBinding

class NewsAdapter(private var newsList: List<NewsArticle>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ItemNewsCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.binding.apply {
            tvNewsTitle.text = article.title
            tvCategory.text = article.category
            tvSourceAndTime.text = "${article.source} • ${article.time}"

            Glide.with(ivNewsImage.context)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_newspaper)
                .into(ivNewsImage)
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newList: List<NewsArticle>) {
        this.newsList = newList
        notifyDataSetChanged()
    }
}
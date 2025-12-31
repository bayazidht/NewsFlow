package com.bayazidht.newsflow.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.AppDatabase
import com.bayazidht.newsflow.data.NewsItem
import com.bumptech.glide.Glide
import com.bayazidht.newsflow.databinding.ItemNewsCardBinding
import com.bayazidht.newsflow.ui.activity.NewsDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsAdapter(private var newsList: List<NewsItem>) :
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

            val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                article.time,
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS
            ).toString()

            tvSourceAndTime.text = "${article.source} • ${relativeTime}"

            Glide.with(ivNewsImage.context)
                .load(article.imageUrl)
                .placeholder(R.drawable.news_placeholder)
                .into(ivNewsImage)
        }

        val db = AppDatabase.getDatabase(holder.itemView.context)
        CoroutineScope(Dispatchers.IO).launch {
            val isSaved = db.bookmarkDao().isBookmarked(article.title)
            withContext(Dispatchers.Main) {
                if (isSaved) {
                    holder.binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                } else {
                    holder.binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline)
                }
            }
        }

        holder.binding.btnBookmark.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val isSaved = db.bookmarkDao().isBookmarked(article.title)
                if (isSaved) {
                    db.bookmarkDao().deleteBookmark(article)
                    withContext(Dispatchers.Main) {
                        holder.binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline)
                        Toast.makeText(holder.itemView.context, "Removed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    db.bookmarkDao().insertBookmark(article)
                    withContext(Dispatchers.Main) {
                        holder.binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                        Toast.makeText(holder.itemView.context, "Saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, NewsDetailsActivity::class.java)
            intent.putExtra("news_data", article)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newList: List<NewsItem>) {
        this.newsList = newList
        notifyDataSetChanged()
    }
}
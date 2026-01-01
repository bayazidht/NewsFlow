package com.bayazidht.newsflow.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.local.AppDatabase
import com.bayazidht.newsflow.data.model.NewsItem
import com.bumptech.glide.Glide
import com.bayazidht.newsflow.databinding.ItemNewsCardBinding
import com.bayazidht.newsflow.ui.activity.NewsDetailsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class NewsAdapter(private var newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ItemNewsCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        val context = holder.itemView.context
        val db = AppDatabase.getDatabase(context)

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
                .placeholder(R.drawable.placeholder_news)
                .error(R.drawable.placeholder_error)
                .into(ivNewsImage)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val isSaved = db.bookmarkDao().isBookmarked(article.title)
            withContext(Dispatchers.Main) {
                holder.binding.btnBookmark.setImageResource(
                    if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
                )
            }
        }

        holder.binding.btnBookmark.setOnClickListener {
            toggleBookmark(article, holder, db)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailsActivity::class.java)
            intent.putExtra("news_data", article)
            context.startActivity(intent)
        }
    }

    private fun toggleBookmark(article: NewsItem, holder: NewsViewHolder, db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val wasSaved = db.bookmarkDao().isBookmarked(article.title)

            if (wasSaved) db.bookmarkDao().deleteBookmark(article)
            else db.bookmarkDao().insertBookmark(article)

            withContext(Dispatchers.Main) {
                holder.binding.btnBookmark.setImageResource(
                    if (wasSaved) R.drawable.ic_bookmark_outline else R.drawable.ic_bookmark_filled
                )

                val message = if (wasSaved) "Removed" else "Saved"
                val snackBar = Snackbar.make(holder.itemView, message, Snackbar.LENGTH_LONG)

                val activity = holder.itemView.context as? Activity
                snackBar.anchorView = activity?.findViewById(R.id.bottom_nav)

                snackBar.setAction("Undo") {
                    toggleBookmark(article, holder, db)
                }
                snackBar.show()
            }
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newList: List<NewsItem>) {
        this.newsList = newList
        notifyDataSetChanged()
    }
}
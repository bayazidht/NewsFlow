package com.bayazidht.newsflow.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.AppDatabase
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.databinding.ActivityNewsDetailsBinding
import com.bumptech.glide.Glide
import androidx.core.net.toUri
import kotlinx.coroutines.*

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailsBinding
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val article = intent.getSerializableExtra("news_data") as? NewsItem
        article?.let { setupUI(it) }

        binding.btnBackCard.setOnClickListener { finish() }
    }

    private fun setupUI(article: NewsItem) {
        binding.apply {
            tvDetailsTitle.text = article.title
            tvDetailsCategory.text = article.category.uppercase()
            tvDetailsContent.text = article.content

            val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                article.time, System.currentTimeMillis(), android.text.format.DateUtils.MINUTE_IN_MILLIS
            ).toString()
            tvDetailsSource.text = "${article.source} • $relativeTime"

            Glide.with(this@NewsDetailsActivity)
                .load(article.imageUrl)
                .placeholder(R.drawable.news_placeholder)
                .into(ivDetailsImage)

            checkBookmarkStatus(article)

            btnBookmark.setOnClickListener { toggleBookmark(article) }
            btnShare.setOnClickListener { shareNews(article) }
            btnReadMore.setOnClickListener { openFullArticle(article.articleUrl) }
        }
    }

    private fun checkBookmarkStatus(article: NewsItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val isSaved = db.bookmarkDao().isBookmarked(article.title)
            withContext(Dispatchers.Main) {
                binding.ivBookmark.setImageResource(
                    if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
                )
            }
        }
    }

    private fun toggleBookmark(article: NewsItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val isSaved = db.bookmarkDao().isBookmarked(article.title)
            if (isSaved) db.bookmarkDao().deleteBookmark(article)
            else db.bookmarkDao().insertBookmark(article)

            withContext(Dispatchers.Main) {
                checkBookmarkStatus(article)
            }
        }
    }

    private fun shareNews(article: NewsItem) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, article.title)
            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\nRead more at: ${article.articleUrl}")
        }
        startActivity(Intent.createChooser(shareIntent, "Share News via"))
    }

    private fun openFullArticle(url: String) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, url.toUri())
        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
    }
}
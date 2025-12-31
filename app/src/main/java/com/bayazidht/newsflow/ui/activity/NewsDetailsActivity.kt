package com.bayazidht.newsflow.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.databinding.ActivityNewsDetailsBinding
import com.bumptech.glide.Glide
import androidx.core.net.toUri

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val article = intent.getSerializableExtra("news_data") as? NewsItem

        article?.let {
            binding.tvDetailsTitle.text = it.title

            val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                it.time,
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS
            ).toString()

            binding.tvDetailsSource.text = "${it.source} • ${relativeTime}"
            binding.tvDetailsCategory.text = it.category
            binding.tvDetailsContent.text = it.content

            Glide.with(this)
                .load(it.imageUrl)
                .placeholder(R.drawable.news_placeholder)
                .into(binding.ivDetailsImage)
        }

        binding.btnReadMore.setOnClickListener {
            article?.articleUrl?.let { url ->
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, url.toUri())
            }
        }

        binding.btnBackCard.setOnClickListener { finish() }
    }
}
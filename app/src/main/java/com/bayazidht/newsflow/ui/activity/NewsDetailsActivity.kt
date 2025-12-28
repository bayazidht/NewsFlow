package com.bayazidht.newsflow.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsArticle
import com.bayazidht.newsflow.databinding.ActivityNewsDetailsBinding
import com.bumptech.glide.Glide

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

        val article = intent.getSerializableExtra("news_data") as? NewsArticle

        article?.let {
            binding.tvDetailsTitle.text = it.title
            binding.tvDetailsSource.text = "${it.source} • ${it.time}"
            binding.tvDetailsCategory.text = it.category
            binding.tvDetailsContent.text = it.content

            Glide.with(this).load(it.imageUrl).into(binding.ivDetailsImage)
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
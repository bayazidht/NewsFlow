package com.bayazidht.newsflow.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.data.NewsSources
import com.bayazidht.newsflow.data.RssParser
import com.bayazidht.newsflow.databinding.FragmentTrendingBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class TrendingFragment : Fragment(R.layout.fragment_trending) {

    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!
    private lateinit var trendingAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrendingBinding.bind(view)

        setupRecyclerView()
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadTrendingNews(NewsSources.getTrendingSources())
        }
        loadTrendingNews(NewsSources.getTrendingSources())
    }

    private fun setupRecyclerView() {
        trendingAdapter = NewsAdapter(emptyList())
        binding.rvTrending.apply {
            adapter = trendingAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadTrendingNews(trendingSources: List<String>) {
        binding.swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch(Dispatchers.IO) {
            val allTrendingNews = mutableListOf<NewsItem>()
            val parser = RssParser()

            val jobs = trendingSources.map { url ->
                async {
                    try {
                        parser.fetchRss(url)
                    } catch (_: Exception) {
                        emptyList()
                    }
                }
            }

            val results = jobs.awaitAll()
            results.forEach { allTrendingNews.addAll(it) }

            allTrendingNews.sortByDescending { it.time }

            val finalNewsList = allTrendingNews.distinctBy { it.title }

            withContext(Dispatchers.Main) {
                binding.swipeRefreshLayout.isRefreshing = false

                if (finalNewsList.isNotEmpty()) {

                    val hero = finalNewsList[0]
                    binding.tvHeroTitle.text = hero.title

                    Glide.with(this@TrendingFragment)
                        .load(hero.imageUrl)
                        .placeholder(R.drawable.news_placeholder)
                        .into(binding.ivHeroImage)

                    if (finalNewsList.size > 1) {
                        val subList = finalNewsList.drop(1)
                        trendingAdapter.updateData(subList)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
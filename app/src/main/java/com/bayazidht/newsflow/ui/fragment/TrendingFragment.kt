package com.bayazidht.newsflow.ui.fragment

import android.content.Context
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

    private var cachedTrendingNews: List<NewsItem> = emptyList()
    private var lastRegion: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrendingBinding.bind(view)

        setupRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchTrendingByRegion()
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = requireActivity().getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
        val currentRegion = sharedPref.getString("user_region", "Global") ?: "Global"

        if (currentRegion != lastRegion || cachedTrendingNews.isEmpty()) {
            lastRegion = currentRegion
            fetchTrendingByRegion()
        } else {
            displayNews(cachedTrendingNews)
        }
    }

    private fun fetchTrendingByRegion() {
        val sources = NewsSources.regionSources[lastRegion] ?: NewsSources.regionSources["Global"]!!
        loadTrendingNews(sources)
    }

    private fun loadTrendingNews(trendingSources: List<String>) {
        binding.swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch(Dispatchers.IO) {
            val allTrendingNews = mutableListOf<NewsItem>()
            val parser = RssParser()

            val jobs = trendingSources.map { url ->
                async {
                    try { parser.fetchRss(url) } catch (_: Exception) { emptyList() }
                }
            }

            val results = jobs.awaitAll()
            results.forEach { allTrendingNews.addAll(it) }

            allTrendingNews.sortByDescending { it.time }
            val finalNewsList = allTrendingNews.distinctBy { it.title }

            withContext(Dispatchers.Main) {
                if (_binding != null) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (finalNewsList.isNotEmpty()) {
                        cachedTrendingNews = finalNewsList
                        displayNews(finalNewsList)

                        binding.trendingContent.visibility = View.VISIBLE
                        binding.layoutEmptyState.visibility = View.GONE
                    } else {
                        binding.trendingContent.visibility = View.GONE
                        binding.layoutEmptyState.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun displayNews(newsList: List<NewsItem>) {
        if (newsList.isEmpty()) return

        val hero = newsList[0]
        binding.tvHeroTitle.text = hero.title
        Glide.with(this).load(hero.imageUrl).placeholder(R.drawable.news_placeholder).into(binding.ivHeroImage)

        if (newsList.size > 1) {
            trendingAdapter.updateData(newsList.drop(1))
        }
    }

    private fun setupRecyclerView() {
        trendingAdapter = NewsAdapter(emptyList())
        binding.rvTrending.apply {
            adapter = trendingAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
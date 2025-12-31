package com.bayazidht.newsflow.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.databinding.FragmentHomeBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import androidx.lifecycle.lifecycleScope
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.data.RssParser
import kotlinx.coroutines.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    private val rssSources = listOf(
        "https://www.thedailystar.net/historical/front-page/rss.xml",
        "https://www.aljazeera.com/xml/rss/all.xml",
        "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        loadMultipleNewsSources()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadMultipleNewsSources() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val allNews = mutableListOf<NewsItem>()
            val parser = RssParser()

            val jobs = rssSources.map { url ->
                async {
                    try {
                        parser.fetchRss(url)
                    } catch (_: Exception) {
                        emptyList()
                    }
                }
            }

            val results = jobs.awaitAll()
            results.forEach { allNews.addAll(it) }

            allNews.sortByDescending { it.time }

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE

                if (allNews.isNotEmpty()) {
                    val uniqueNews = allNews.distinctBy { it.title }
                    newsAdapter.updateData(uniqueNews)
                } else {
                    Toast.makeText(context, "No News Found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
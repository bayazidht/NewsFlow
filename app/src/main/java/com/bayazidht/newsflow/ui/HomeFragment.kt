package com.bayazidht.newsflow.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsArticle
import com.bayazidht.newsflow.databinding.FragmentHomeBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter

import androidx.lifecycle.lifecycleScope
import com.bayazidht.newsflow.data.RssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        //loadData()
        loadLiveNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadData() {
        val dummyList = listOf(
            NewsArticle("Global Markets Rally as Inflation Cools", "BUSINESS", "CNBC", "1h ago", "https://via.placeholder.com/600x400"),
            NewsArticle("New Tech Summit to Discuss AI Ethics", "TECH", "The Verge", "3h ago", "https://via.placeholder.com/600x400"),
            NewsArticle("Upcoming Elections: What You Need to Know", "POLITICS", "BBC", "5h ago", "https://via.placeholder.com/600x400")
        )
        newsAdapter.updateData(dummyList)
    }

    private fun loadLiveNews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val rssUrl = "https://www.thedailystar.net/historical/front-page/rss.xml"
            val parser = RssParser()
            val newsList = parser.fetchRss(rssUrl)

            Log.d("RSS_CHECK", "Total News Found: ${newsList.size}")

            withContext(Dispatchers.Main) {
                if (newsList.isNotEmpty()) {
                    newsAdapter.updateData(newsList)
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
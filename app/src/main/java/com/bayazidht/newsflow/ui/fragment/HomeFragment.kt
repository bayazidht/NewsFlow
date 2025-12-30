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

    private fun loadLiveNews() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val rssUrl = "https://www.thedailystar.net/historical/front-page/rss.xml"
            val newsList = RssParser().fetchRss(rssUrl)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
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
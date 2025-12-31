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
import com.bayazidht.newsflow.data.NewsSources
import com.bayazidht.newsflow.data.RssParser
import kotlinx.coroutines.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            if (checkedId != null) {
                val chip = group.findViewById<com.google.android.material.chip.Chip>(checkedId)
                val categoryName = chip.text.toString()
                val sources = NewsSources.getSourcesByCategory(categoryName)
                loadMultipleNewsSources(sources)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val checkedChipId = binding.chipGroup.checkedChipId
            val chip = binding.chipGroup.findViewById<com.google.android.material.chip.Chip>(checkedChipId)
            val categoryName = chip?.text?.toString() ?: "All"
            val sources = NewsSources.getSourcesByCategory(categoryName)
            loadMultipleNewsSources(sources)
        }

        loadMultipleNewsSources(NewsSources.getSourcesByCategory("All"))
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadMultipleNewsSources(rssSources: List<String>) {
        binding.swipeRefreshLayout.isRefreshing = true

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
                binding.swipeRefreshLayout.isRefreshing = false
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
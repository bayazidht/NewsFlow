package com.bayazidht.newsflow.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.data.NewsSources
import com.bayazidht.newsflow.data.RssParser
import com.bayazidht.newsflow.databinding.FragmentHomeBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import kotlinx.coroutines.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    private var userRegion: String = "Global"
    private var userInterests: Set<String> = emptySet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()

        binding.chipGroup.setOnCheckedStateChangeListener { _, _ ->
            refreshCurrentCategory()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshCurrentCategory()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserPreferences()
        refreshCurrentCategory()
    }

    private fun loadUserPreferences() {
        val sharedPref = requireActivity().getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("dark_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        userRegion = sharedPref.getString("user_region", "Global") ?: "Global"
        userInterests = sharedPref.getStringSet("selected_interests", emptySet()) ?: emptySet()
    }

    private fun refreshCurrentCategory() {
        val checkedChipId = binding.chipGroup.checkedChipId
        val chip = binding.chipGroup.findViewById<com.google.android.material.chip.Chip>(checkedChipId)
        val categoryName = chip?.text?.toString() ?: "All"

        val sources = NewsSources.getPersonalizedSources(categoryName, userRegion, userInterests)

        loadMultipleNewsSources(sources)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun loadMultipleNewsSources(rssSources: List<String>) {
        if (rssSources.isEmpty()) {
            binding.swipeRefreshLayout.isRefreshing = false
            return
        }
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
                if (_binding != null) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (allNews.isNotEmpty()) {
                        val uniqueNews = allNews.distinctBy { it.title }
                        newsAdapter.updateData(uniqueNews)
                        binding.rvNews.scrollToPosition(0)

                        binding.rvNews.visibility = View.VISIBLE
                        binding.layoutEmptyState.visibility = View.GONE
                    } else {
                        newsAdapter.updateData(emptyList())
                        binding.rvNews.visibility = View.GONE
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        Toast.makeText(context, "No news found for $userRegion", Toast.LENGTH_SHORT).show()
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
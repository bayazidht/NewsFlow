package com.bayazidht.newsflow.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.repository.NewsDataHolder
import com.bayazidht.newsflow.data.model.NewsItem
import com.bayazidht.newsflow.data.remote.NewsSources
import com.bayazidht.newsflow.data.remote.RssParser
import com.bayazidht.newsflow.databinding.FragmentHomeBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    private var userRegion: String = "Global"
    private var userInterests: Set<String> = emptySet()

    private var lastRegion: String = ""
    private var lastInterests: Set<String> = emptySet()

    private var cachedNews: List<NewsItem> = emptyList()

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

        if (cachedNews.isNotEmpty()) {
            newsAdapter.updateData(cachedNews)
            toggleViews(true)
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = requireActivity().getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
        val currentRegion = sharedPref.getString("user_region", "Global") ?: "Global"
        val currentInterests = sharedPref.getStringSet("selected_interests", emptySet()) ?: emptySet()

        applyDarkMode(sharedPref.getBoolean("dark_mode", false))

        if (currentRegion != lastRegion || currentInterests != lastInterests || cachedNews.isEmpty()) {
            userRegion = currentRegion
            userInterests = currentInterests
            lastRegion = currentRegion
            lastInterests = currentInterests

            refreshCurrentCategory()
        }
    }

    private fun refreshCurrentCategory() {
        val checkedChipId = binding.chipGroup.checkedChipId
        val chip = binding.chipGroup.findViewById<com.google.android.material.chip.Chip>(checkedChipId)
        val categoryName = chip?.text?.toString() ?: "All"

        val sources = NewsSources.getPersonalizedSources(categoryName, userRegion, userInterests)
        loadMultipleNewsSources(sources)
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
                    try { parser.fetchRss(url) } catch (_: Exception) { emptyList() }
                }
            }

            val results = jobs.awaitAll()
            results.forEach { allNews.addAll(it) }
            allNews.sortByDescending { it.time }

            withContext(Dispatchers.Main) {
                if (_binding != null) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (allNews.isNotEmpty()) {
                        cachedNews = allNews.distinctBy { it.title }
                        newsAdapter.updateData(cachedNews)
                        binding.rvNews.scrollToPosition(0)
                        toggleViews(true)

                        val updatedList = NewsDataHolder.allNewsList + cachedNews
                        NewsDataHolder.allNewsList = updatedList.distinctBy { it.articleUrl }
                    } else {
                        cachedNews = emptyList()
                        newsAdapter.updateData(emptyList())
                        toggleViews(false)
                        showSnackBar("No news found for $userRegion")
                    }
                }
            }
        }
    }

    private fun toggleViews(hasData: Boolean) {
        if (hasData) {
            binding.rvNews.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        } else {
            binding.rvNews.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackBar.anchorView = requireActivity().findViewById(R.id.bottom_nav)
        snackBar.setAction("Retry") {
            refreshCurrentCategory()
        }
        snackBar.show()
    }

    private fun applyDarkMode(isDark: Boolean) {
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
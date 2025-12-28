package com.bayazidht.newsflow.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.RssParser
import com.bayazidht.newsflow.databinding.FragmentRecentBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentFragment : Fragment(R.layout.fragment_recent) {

    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecentBinding.bind(view)

        setupRecyclerView()
        loadRecentNews()
    }

    private fun setupRecyclerView() {
        recentAdapter = NewsAdapter(emptyList())
        binding.rvRecent.apply {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadRecentNews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val rssUrl = "https://www.aljazeera.com/xml/rss/all.xml"
            val newsList = RssParser().fetchRss(rssUrl)

            withContext(Dispatchers.Main) {
                if (newsList.isNotEmpty()) {
                    recentAdapter.updateData(newsList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.bayazidht.newsflow.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.RssParser
import com.bayazidht.newsflow.databinding.FragmentTrendingBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendingFragment : Fragment(R.layout.fragment_trending) {

    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!
    private lateinit var trendingAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrendingBinding.bind(view)

        setupRecyclerView()
        loadTrendingNews()
    }

    private fun setupRecyclerView() {
        trendingAdapter = NewsAdapter(emptyList())
        binding.rvTrending.apply {
            adapter = trendingAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun loadTrendingNews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val rssUrl = "https://www.aljazeera.com/xml/rss/all.xml"
            val newsList = RssParser().fetchRss(rssUrl)

            withContext(Dispatchers.Main) {
                if (newsList.isNotEmpty()) {
                    val hero = newsList[0]
                    binding.tvHeroTitle.text = hero.title
                    binding.tvHeroCategory.text = "TRENDING"

                    Glide.with(this@TrendingFragment)
                        .load(hero.imageUrl)
                        .placeholder(R.drawable.ic_newspaper)
                        .into(binding.ivHeroImage)

                    val subList = newsList.drop(1)
                    trendingAdapter.updateData(subList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
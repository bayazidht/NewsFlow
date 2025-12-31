package com.bayazidht.newsflow.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.data.NewsDataHolder
import com.bayazidht.newsflow.data.NewsItem
import com.bayazidht.newsflow.databinding.ActivitySearchBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: NewsAdapter
    private var allNewsList: List<NewsItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.searchHeader) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        allNewsList = NewsDataHolder.allNewsList

        setupRecyclerView()
        setupSearchView()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        searchAdapter = NewsAdapter(emptyList())
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            requestFocus()

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    filterNews(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterNews(newText)
                    return true
                }
            })
        }
    }

    private fun filterNews(query: String?) {
        query?.takeIf { it.isNotBlank() }?.let { safeQuery ->
            val searchQuery = safeQuery.trim()

            val filteredList = allNewsList.filter { item ->
                item.title.contains(searchQuery, ignoreCase = true) ||
                        item.content.contains(searchQuery, ignoreCase = true) ||
                        item.source.contains(searchQuery, ignoreCase = true) ||
                        item.category.contains(searchQuery, ignoreCase = true)
            }

            if (filteredList.isEmpty()) {
                searchAdapter.updateData(emptyList())
                binding.layoutEmptySearch.visibility = View.VISIBLE
            } else {
                searchAdapter.updateData(filteredList)
                binding.layoutEmptySearch.visibility = View.GONE
            }
        } ?: run {
            searchAdapter.updateData(emptyList())
            binding.layoutEmptySearch.visibility = View.GONE
        }
    }
}
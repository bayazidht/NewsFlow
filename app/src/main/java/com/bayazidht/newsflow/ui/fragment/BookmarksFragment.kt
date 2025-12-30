package com.bayazidht.newsflow.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.AppDatabase
import com.bayazidht.newsflow.databinding.FragmentBookmarksBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import kotlinx.coroutines.launch

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookmarksBinding.bind(view)

        setupRecyclerView()
        observeBookmarks()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvBookmarks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun observeBookmarks() {
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            db.bookmarkDao().getAllBookmarks().collect { savedNews ->
                if (savedNews.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvBookmarks.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvBookmarks.visibility = View.VISIBLE
                    newsAdapter.updateData(savedNews)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
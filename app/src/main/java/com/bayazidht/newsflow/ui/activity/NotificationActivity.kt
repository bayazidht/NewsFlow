package com.bayazidht.newsflow.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.data.local.AppDatabase
import com.bayazidht.newsflow.data.model.NewsItem
import com.bayazidht.newsflow.databinding.ActivityNotificationBinding
import com.bayazidht.newsflow.ui.adapter.NewsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var newsAdapter: NewsAdapter
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.btnClearAll.setOnClickListener { showClearAllDialog() }

        setupRecyclerView()
        observeNotifications()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNotifications.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NotificationActivity)
            setHasFixedSize(true)
        }
    }

    private fun observeNotifications() {
        lifecycleScope.launch {
            db.notificationDao().getAllNotifications().collect { historyList ->
                if (historyList.isNotEmpty()) {
                    binding.rvNotifications.visibility = View.VISIBLE
                    binding.layoutEmptyState.visibility = View.GONE

                    val adapterList = historyList.map {
                        NewsItem(
                            title = it.title,
                            category = it.category,
                            source = it.source,
                            time = it.time,
                            imageUrl = it.imageUrl,
                            content = it.content,
                            articleUrl = it.articleUrl
                        )
                    }
                    newsAdapter.updateData(adapterList)
                } else {
                    binding.rvNotifications.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.VISIBLE
                    newsAdapter.updateData(emptyList())
                }
            }
        }
    }

    private fun showClearAllDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear History")
            .setMessage("Are you sure you want to delete all notifications?")
            .setPositiveButton("Clear") { _, _ ->
                lifecycleScope.launch {
                    db.notificationDao().clearAll()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
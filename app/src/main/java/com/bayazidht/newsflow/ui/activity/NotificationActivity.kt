package com.bayazidht.newsflow.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.AppDatabase
import com.bayazidht.newsflow.data.NewsItem
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
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeNotifications()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
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
            // Flow ব্যবহার করার ফলে ডাটাবেসে নতুন ডাটা আসলেই অটোমেটিক আপডেট হবে
            db.notificationDao().getAllNotifications().collect { historyList ->
                if (historyList.isNotEmpty()) {
                    binding.rvNotifications.visibility = View.VISIBLE
                    binding.layoutEmptyState.visibility = View.GONE

                    // NotificationNewsItem -> NewsItem রূপান্তর (ম্যাপিং)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear_all) {
            showClearAllDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
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
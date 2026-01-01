package com.bayazidht.newsflow.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bayazidht.newsflow.R
import com.bayazidht.newsflow.data.AppDatabase
import com.bayazidht.newsflow.data.NewsSources
import com.bayazidht.newsflow.data.NotificationItem
import com.bayazidht.newsflow.data.RssParser
import com.bayazidht.newsflow.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class NewsWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val sharedPref = applicationContext.getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
                val userRegion = sharedPref.getString("user_region", "Global") ?: "Global"
                val userInterests = sharedPref.getStringSet("selected_interests", emptySet()) ?: emptySet()

                val personalizedSources = NewsSources.getPersonalizedSources("All", userRegion, userInterests)

                if (personalizedSources.isNotEmpty()) {
                    val parser = RssParser()
                    val jobs = personalizedSources.map { url ->
                        async {
                            try { parser.fetchRss(url) } catch (_: Exception) { emptyList() }
                        }
                    }
                    val results = jobs.awaitAll()
                    val allNews = results.flatten().sortedByDescending { it.time }

                    if (allNews.isNotEmpty()) {
                        val latestNews = allNews[0]
                        showNotification(latestNews.source, latestNews.title)

                        val db = AppDatabase.getDatabase(applicationContext)
                        db.notificationDao().insertNotification(
                            NotificationItem(
                                articleUrl = latestNews.articleUrl,
                                title = latestNews.title,
                                category = "Breaking",
                                source = latestNews.source,
                                time = latestNews.time,
                                imageUrl = latestNews.imageUrl,
                                content = latestNews.content
                            )
                        )
                    }
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "news_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "News Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for latest personalized news updates"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_newspaper)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
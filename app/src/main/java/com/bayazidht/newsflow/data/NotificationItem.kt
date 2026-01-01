package com.bayazidht.newsflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_history")
data class NotificationItem(
    @PrimaryKey val articleUrl: String,
    val title: String,
    val category: String,
    val source: String,
    val time: Long,
    val imageUrl: String,
    val content: String
)

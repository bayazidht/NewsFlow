package com.bayazidht.newsflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bookmarks")
data class NewsItem(
    @PrimaryKey val articleUrl: String,
    val title: String,
    val category: String,
    val source: String,
    val time: Long,
    val imageUrl: String,
    val content: String
): Serializable


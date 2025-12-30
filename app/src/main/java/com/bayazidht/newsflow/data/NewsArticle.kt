package com.bayazidht.newsflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bookmarks")
data class NewsArticle(
    @PrimaryKey val title: String,
    val category: String,
    val source: String,
    val time: String,
    val imageUrl: String,
    val content: String,
    val articleUrl: String
): Serializable


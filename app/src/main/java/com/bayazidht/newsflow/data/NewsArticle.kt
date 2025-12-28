package com.bayazidht.newsflow.data

import java.io.Serializable

data class NewsArticle(
    val title: String,
    val category: String,
    val source: String,
    val time: String,
    val imageUrl: String,
    val content: String,
    val articleUrl: String
): Serializable

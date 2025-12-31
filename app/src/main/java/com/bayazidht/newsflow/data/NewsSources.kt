package com.bayazidht.newsflow.data

object NewsSources {
    fun getSourcesByCategory(category: String): List<String> {
        return categories[category] ?: categories["All"]!!
    }
    val categories = mapOf(
        "All" to listOf(
            "http://feeds.bbci.co.uk/news/rss.xml",
            "https://www.aljazeera.com/xml/rss/all.xml",
            "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en"
        ),
        "Technology" to listOf(
            "https://www.theverge.com/rss/index.xml",
            "https://techcrunch.com/feed/",
            "https://www.wired.com/feed/rss"
        ),
        "Business" to listOf(
            "https://www.cnbc.com/id/100003114/device/rss/rss.html",
            "http://feeds.reuters.com/reuters/businessNews",
            "https://fortune.com/feed/"
        ),
        "Sports" to listOf(
            "https://www.espn.com/espn/rss/news",
            "https://api.foxsports.com/v1/rss?tag=soccer",
            "https://www.skysports.com/rss/12040"
        ),
        "Lifestyle" to listOf(
            "https://rss.nytimes.com/services/xml/rss/nyt/Health.xml",
            "https://www.medicalnewstoday.com/rss/top-news"
        ),
        "Entertainment" to listOf(
            "https://www.cnbc.com/id/100003114/device/rss/rss.html",
            "http://feeds.reuters.com/reuters/businessNews",
            "https://fortune.com/feed/"
        )
    )

    fun getTrendingSources(): List<String> {
        return listOf(
            "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en",
            "https://www.aljazeera.com/xml/rss/all.xml",
            "http://feeds.bbci.co.uk/news/rss.xml"
        )
    }


}
package com.bayazidht.newsflow.data

object NewsSources {

    val regionSources = mapOf(
        "Global" to listOf(
            "http://feeds.bbci.co.uk/news/rss.xml",
            "https://www.aljazeera.com/xml/rss/all.xml",
            "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en"
        ),
        "North America" to listOf(
            "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml",
            "https://abcnews.go.com/abcnews/topstories",
            "http://feeds.foxnews.com/foxnews/latest"
        ),
        "Europe" to listOf(
            "http://feeds.bbci.co.uk/news/rss.xml",
            "https://www.theguardian.com/world/rss",
            "https://www.euronews.com/rss?level=vertical&name=news"
        ),
        "Asia Pacific" to listOf(
            "https://www.scmp.com/rss/91/feed",
            "https://www.japantimes.co.jp/feed/",
            "https://www.channelnewsasia.com/rssfeed/8395986"
        ),
        "Middle East" to listOf(
            "https://www.aljazeera.com/xml/rss/all.xml",
            "https://www.khaleejtimes.com/rss-feeds",
            "https://tbsnews.net/middle-east/rss.xml"
        ),
        "Africa" to listOf(
            "https://allafrica.com/tools/headlines/rdf/latest/main.html",
            "https://www.africanews.com/rss"
        )
    )

    val categories = mapOf(
        "Technology" to listOf(
            "https://www.theverge.com/rss/index.xml",
            "https://techcrunch.com/feed/",
            "https://www.wired.com/feed/rss"
        ),
        "Business" to listOf(
            "https://www.cnbc.com/id/100003114/device/rss/rss.html",
            "https://fortune.com/feed/"
        ),
        "Sports" to listOf(
            "https://www.espn.com/espn/rss/news",
            "https://www.skysports.com/rss/12040"
        ),
        "Entertainment" to listOf(
            "https://variety.com/feed/",
            "https://www.hollywoodreporter.com/feed/"
        )
    )

    fun getPersonalizedSources(
        category: String,
        userRegionChipName: String,
        userInterests: Set<String>
    ): List<String> {
        val sources = mutableListOf<String>()

        if (category == "All") {
            sources.addAll(regionSources[userRegionChipName] ?: regionSources["Global"]!!)
            userInterests.forEach { interest ->
                categories[interest]?.let { sources.addAll(it) }
            }
        } else {
            sources.addAll(categories[category] ?: regionSources["Global"]!!)
        }
        return sources.distinct()
    }
}
package com.bayazidht.newsflow.data

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL

class RssParser {
    fun fetchRss(urlPath: String): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        try {
            val url = URL(urlPath)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(connection.inputStream, "UTF-8")

            var eventType = parser.eventType
            var title = ""
            var image = ""
            var pubDate = ""
            var source = "NewsFlow"
            var description = ""
            var insideItem = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name.equals("item", true)) {
                            insideItem = true
                        } else if (insideItem) {
                            when {
                                name.equals("title", true) -> title = parser.nextText()
                                name.equals("pubDate", true) -> {
                                    val rawDate = parser.nextText()
                                    pubDate = formatRssDate(rawDate)
                                }
                                name.equals("description", true) || name.equals("content:encoded", true) || name.equals("summary", true) -> {
                                    description = parser.nextText()
                                }
                                name.equals("source", true) -> source = parser.nextText()
                                name.equals("enclosure", true) || name.equals("media:content", true) -> {
                                    val urlAttr = parser.getAttributeValue(null, "url")
                                    if (!urlAttr.isNullOrEmpty()) image = urlAttr
                                }
                                name.equals("media:thumbnail", true) -> {
                                    if (image.isEmpty()) {
                                        image = parser.getAttributeValue(null, "url") ?: ""
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (name.equals("item", true)) {
                            if (title.isNotEmpty()) {
                                val cleanContent = description.replace(Regex("<[^>]*>"), "").trim()
                                articles.add(NewsArticle(
                                    title = title.trim(),
                                    category = "LATEST",
                                    source = source,
                                    time = pubDate,
                                    imageUrl = image,
                                    content = cleanContent
                                ))
                            }
                            insideItem = false
                            title = ""; image = ""; pubDate = ""; source = "NewsFlow"
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RSS_PARSER", "Error: ${e.message}")
            e.printStackTrace()
        }
        return articles
    }

    private fun formatRssDate(dateString: String): String {
        return try {
            if (dateString.length > 16) dateString.substring(5, 16) else dateString
        } catch (_: Exception) {
            dateString
        }
    }
}
package com.bayazidht.newsflow.data

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class RssParser {
    fun fetchRss(urlPath: String): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        try {
            val url = URL(urlPath)
            val factory = XmlPullParserFactory.newInstance()

            factory.isNamespaceAware = false
            val parser = factory.newPullParser()

            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()

            parser.setInput(inputStream, "UTF-8")

            var eventType = parser.eventType
            var title = ""; var image = ""; var insideItem = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name.equals("item", true)) {
                            insideItem = true
                        } else if (insideItem) {
                            when {
                                name.equals("title", true) -> title = parser.nextText()
                                // ইমেজের জন্য enclosure বা media:content চেক করা
                                name.equals("enclosure", true) || name.equals("media:content", true) -> {
                                    image = parser.getAttributeValue(null, "url") ?: ""
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (name.equals("item", true)) {
                            if (title.isNotEmpty()) {
                                articles.add(NewsArticle(title, "LATEST", "NewsFlow", "Just now", image))
                                Log.d("RSS_DEBUG", "Found Article: $title")
                            }
                            insideItem = false
                            title = ""; image = ""
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RSS_DEBUG", "Error parsing: ${e.message}")
        }
        return articles
    }
}
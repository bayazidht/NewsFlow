package com.bayazidht.newsflow.data.remote

import android.util.Log
import com.bayazidht.newsflow.data.model.NewsItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class RssParser {
    fun fetchRss(urlPath: String): List<NewsItem> {
        val articles = mutableListOf<NewsItem>()
        val domainSource = try {
            URL(urlPath).host.replace("www.", "").split(".")[0].uppercase()
        } catch (_: Exception) { "NEWS" }

        try {
            val url = URL(urlPath)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 15000
                readTimeout = 15000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            connection.connect()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(connection.inputStream, "UTF-8")

            var eventType = parser.eventType
            var insideItem = false
            var title = ""
            var image = ""
            var pubDate = 0L
            var description = ""
            var link = ""
            var category = "GLOBAL"

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName.equals("item", true)) {
                            insideItem = true
                        } else if (insideItem) {
                            when {
                                tagName.equals("title", true) -> title = parser.nextText()
                                tagName.equals("link", true) -> link = parser.nextText()
                                tagName.equals("pubDate", true) -> pubDate = formatRssDate(parser.nextText())
                                tagName.equals("category", true) -> category = parser.nextText()

                                tagName.equals("enclosure", true) -> {
                                    val attrUrl = parser.getAttributeValue(null, "url")
                                    val attrType = parser.getAttributeValue(null, "type")
                                    if (attrType?.contains("image") == true) image = attrUrl
                                }

                                tagName.contains("media:content", true) ||
                                        tagName.contains("media:thumbnail", true) -> {
                                    val attrUrl = parser.getAttributeValue(null, "url")
                                    if (!attrUrl.isNullOrEmpty()) image = attrUrl
                                }

                                tagName.equals("description", true) -> {
                                    val desc = parser.nextText()
                                    description = desc
                                    if (image.isEmpty()) image = extractImageUrlFromHtml(desc)
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName.equals("item", true)) {
                            if (title.isNotEmpty()) {
                                val cleanContent = description.replace(Regex("<[^>]*>"), "").trim()

                                articles.add(
                                    NewsItem(
                                        title = title.trim(),
                                        category = category.uppercase(),
                                        source = domainSource,
                                        time = pubDate,
                                        imageUrl = image,
                                        content = cleanContent,
                                        articleUrl = link
                                    )
                                )
                            }
                            title = ""
                            image = ""
                            pubDate = 0L
                            description = ""
                            link = ""
                            category = "GLOBAL"
                            insideItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RSS_PARSER", "Error parsing $urlPath: ${e.message}")
        }
        return articles
    }

    private fun extractImageUrlFromHtml(html: String): String {
        return try {
            val regex = Regex("src=\"([^\"]+)\"")
            val match = regex.find(html)
            var url = match?.groups?.get(1)?.value ?: ""
            if (url.startsWith("//")) url = "https:$url"
            url
        } catch (e: Exception) { "" }
    }

    private fun formatRssDate(dateString: String): Long {
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "EEE, d MMM yyyy HH:mm:ss Z"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                val date = sdf.parse(dateString)
                if (date != null) return date.time
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }
}
package com.zelda.hackernewsandroid

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

object ContentExtractor {

    private val client = OkHttpClient()

    fun fetchContent(url: String?): String {
        val request = Request.Builder()
            .url(url.toString())
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to download content: $url")

            val htmlContent = response.body?.string() ?: ""
            return extractTextFromHtml(htmlContent)
        }
    }

    private fun extractTextFromHtml(html: String): String {
        val document = Jsoup.parse(html)
        return document.text() // extracts all the text from the HTML for now
    }
}

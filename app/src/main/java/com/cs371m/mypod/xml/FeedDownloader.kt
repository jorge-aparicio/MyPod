package com.cs371m.mypod.xml

import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FeedDownloader{

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun loadXmlFromNetwork(urlString: String): List<RssParser.Channel> {
        // Checks whether the user set the preference to include summary text


        val channels: List<RssParser.Channel> = downloadUrl(urlString)?.use { stream ->
            // Instantiate the parser
           RssParser().parse(stream)
        } ?: emptyList()

       return channels
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    @Throws(IOException::class)
    suspend fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
        }
    }


}
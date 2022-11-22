package com.cs371m.mypod.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class RssParser {

    private val ns: String? = null
    data class Channel(val title: String?, val description: String?, val items: List<Item>?)
    data class Item  (val title: String?, val episodeNum: String?, val duration: String?, val image: String?, val audioUrl: String?, val pubDate: String?, val guid: String? )
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<Channel> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Channel> {
        val entries = mutableListOf<Channel>()

        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "channel") {
                entries.add(readChannel(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readChannel(parser: XmlPullParser): Channel {
        parser.require(XmlPullParser.START_TAG, ns, "channel")
        var title: String? = null
        var summary: String? = null
        var items = ArrayList<Item>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readTitle(parser)
                "description" -> summary = readDescription(parser)
                "item" -> items.add(readItem(parser))
                else -> skip(parser)
            }
        }
        return Channel(title, summary, items)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }



    // Processes summary tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "description")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "description")
        return summary
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

//    // Processes link tags in the feed.
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readItems(parser: XmlPullParser): List<Item> {
//        var items = ArrayList<Item>()
//        parser.require(XmlPullParser.START_TAG, ns, "items")
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            when (parser.name) {
//                "item" -> items.add(readItem(parser))
//                else -> skip(parser)
//            }
//
//        }
//        parser.require(XmlPullParser.END_TAG, ns, "items")
//        return items
//    }



    // Processes link tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readEnclosure(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, ns, "enclosure")
        val tag = parser.name
        if (tag == "enclosure") {
                link = parser.getAttributeValue(null, "url")
                parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, ns, "enclosure")
        return link
    }

    // Processes link tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItem(parser: XmlPullParser): Item {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title: String? = null
        var episodeNum: String? = null
        var duration: String? = null
        var image: String? = null
        var audioUrl: String? = null
        var pubDate: String? = null
        var guid: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            val parserType = parser.eventType
            val parserName = parser.name
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readTitle(parser)
                "enclosure" -> audioUrl = readEnclosure(parser)
                "itunes:episode" -> episodeNum  = readEpisode(parser)
                "itunes:duration" -> duration = readDuration(parser)
                "pubDate" -> pubDate = readPubDate(parser)
                "itunes:image" -> image = readItunesImage(parser)
                "guid" -> guid = readGUID(parser)
                else -> skip(parser)
            }
        }
        return Item(title, episodeNum, duration, image,audioUrl,pubDate, guid)
    }


    // Processes summary tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDuration(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:duration")
        val duration = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "itunes:duration")
        return duration
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPubDate(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate")
        val pubDate = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "pubDate")
        return pubDate
    }
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readEpisode(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:episode")
        val pubDate = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "itunes:episode")
        return pubDate
    }
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readGUID(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "guid")
        val guid = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "guid")
        return guid
    }
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItunesImage(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, ns, "itunes:image")
        val tag = parser.name
        if (tag == "itunes:image") {
                link = parser.getAttributeValue(null, "href")
        }
        return link
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}
package com.cs371m.mypod.api

import android.util.Log
import androidx.core.text.htmlEncode

class MyPodRepo(private val iTunesAPI: ITunesAPI, private val appleAPI: AppleAPI, private  val feedAPI: FeedAPI) {

    // Search for Podcast Artists
    suspend fun searchPodcasts(term: String, limit: Int) : List<ITunesAPI.Podcast> {
        return iTunesAPI.searchPodcasts(term, limit).results;
    }
    // Lookup a single Podcast Artist
    suspend fun lookupPodcast(id: String) : ITunesAPI.PodcastFeed {
        return iTunesAPI.lookupPodcast(id).results[0];
    }

    suspend fun getTop25(): List<AppleAPI.TopPodcast>{

        return appleAPI.getTopPodcasts().feed.results

    }

    suspend fun getChannel(feedUrl:String): FeedAPI.RssFeed{
       return feedAPI.getFeed(feedUrl)
    }
}
package com.cs371m.mypod.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {


    // Episode Data
    data class PodcastSearchResultsList(val results: List<Podcast>);
    data class PodcastFeedResultsList(val results: List<Podcast>);
    data class Podcast(
        val collectionId: String,
        val collectionName: String,
        var artworkUrl100: String,
        val feedUrl: String?
    )

    @GET("search?media=podcast&entity=podcast")
    suspend fun searchPodcasts(@Query("term") term: String, @Query("limit") limit: Int) : PodcastSearchResultsList;
    // Single Episode Lookup
    @GET("lookup?media=podcast&entity=podcast")
    suspend fun lookupPodcast(@Query("id") id: String) : PodcastFeedResultsList;

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    companion object Factory {
        fun create(): ITunesAPI {
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // Must end in /!
                .baseUrl("https://itunes.apple.com/")
                .build()
            return retrofit.create(ITunesAPI::class.java)
        }
    }

}
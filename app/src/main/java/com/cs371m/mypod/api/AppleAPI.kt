package com.cs371m.mypod.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AppleAPI
{
    data class TopPodcastFeed(val feed:TopPodcastResultsList)
    data class TopPodcastResultsList(val results: List<TopPodcast>)
    data class TopPodcast(val id:String,
                          val name:String,
                          var artworkUrl100: String,
                          )
    @GET("api/v2/us/podcasts/top/10/podcasts.json")
    suspend fun getTopPodcasts() : TopPodcastFeed

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    companion object Factory {
        fun create(): AppleAPI {
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // Must end in /!
                .baseUrl("https://rss.applemarketingtools.com/")
                .build()
            return retrofit.create(AppleAPI::class.java)
        }
    }
}

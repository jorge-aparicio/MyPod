package com.cs371m.mypod.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {


    // Episode Data
    data class PodcastSearchResultsList(val results: List<Podcast>);
    data class Podcast(val collectionId: String,
                       val trackId: String,
                       val artistName: String,
                       val collectionName: String,
                       val trackName: String,
                       val feedUrl: String,
                       val releaseDate: String,
                       val country: String,
                       val primaryGenreName: String,
                       val contentAdvisoryRating: String,
                       var artworkUrl100: String,
                       val genres: List<String>
                       )


    // Image Data
    data class SearchResultImageShell(val results: List<SearchResultImage>);
    data class SearchResultImage(val artworkUrl100: String);

    // Episode Search
    @GET("search?media=podcast&entity=podcast")
    suspend fun searchPodcasts(@Query("term") term: String, @Query("limit") limit: Int) : PodcastSearchResultsList;
    // Single Episode Lookup
    @GET("lookup?media=podcast&entity=podcast")
    suspend fun lookupPodcast(@Query("id") id: String) : PodcastSearchResultsList;

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
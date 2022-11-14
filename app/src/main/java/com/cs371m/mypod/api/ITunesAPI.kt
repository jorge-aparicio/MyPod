package com.cs371m.mypod.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {

    // Artist Data
    data class PodcastArtistSearchResultsList(val results: List<PodcastArtist>);
    data class PodcastArtist(val artistType: String,
                             val artistName: String,
                             val artistLinkUrl: String,
                             val artistId: String,
                             val primaryGenreName: String,
                             var imageUrl: String);

    // Episode Data
    data class EpisodeSearchResultsList(val results: List<Episode>);
    data class Episode(val collectionId: String,
                       val trackId: String,
                       val artistName: String,
                       val collectionName: String,
                       val trackName: String,
                       val feedUrl: String,
                       val releaseDate: String,
                       val country: String,
                       val primaryGenreName: String,
                       val contentAdvisoryRating: String,
                       var artworkUrl600: String,
                       val genres: List<String>
                       )


    // Image Data
    data class SearchResultImageShell(val results: List<SearchResultImage>);
    data class SearchResultImage(val artworkUrl100: String);

    // Podcast Artist Search
    @GET("search?media=podcast&entity=podcastAuthor")
    suspend fun searchPodcastArtists(@Query("term") term: String, @Query("limit") limit: Int) : PodcastArtistSearchResultsList;
    // Single Artist Lookup
    @GET("lookup?media=podcast&entity=podcastAuthor")
    suspend fun lookupPodcastArtist(@Query("id") id: String) : PodcastArtistSearchResultsList;

    // Episode Search
    @GET("search?media=podcast&entity=podcast")
    suspend fun searchEpisodes(@Query("term") term: String, @Query("limit") limit: Int) : EpisodeSearchResultsList;
    // Single Episode Lookup
    @GET("lookup?media=podcast&entity=podcast")
    suspend fun lookupEpisode(@Query("id") id: String) : EpisodeSearchResultsList;

    // Profile Episode List
    @GET("lookup?media=podcast&entity=podcast")
    suspend fun getArtistEpisodes(@Query("id") id: String, @Query("limit") limit: Int) : EpisodeSearchResultsList;

    // Image
    @GET("search?media=podcast&entity=podcast")
    suspend fun getPodcastArtistImage(@Query("term") id: String, @Query("limit") limit: Int) : SearchResultImageShell;

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
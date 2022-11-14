package com.cs371m.mypod.api

import android.util.Log
import androidx.core.text.htmlEncode

class MyPodRepo(private val iTunesAPI: ITunesAPI) {

    // Search for Podcast Artists
    suspend fun searchPodcastArtists(term: String, limit: Int) : List<ITunesAPI.PodcastArtist> {
        return iTunesAPI.searchPodcastArtists(term.htmlEncode(), limit).results;
    }
    // Lookup a single Podcast Artist
    suspend fun lookupPodcastArtist(id: String) : ITunesAPI.PodcastArtist {
        return iTunesAPI.lookupPodcastArtist(id).results[0];
    }

    // Search for Episodes
    suspend fun searchEpisodes(term: String, limit: Int) : List<ITunesAPI.Episode> {
        return iTunesAPI.searchEpisodes(term.htmlEncode(), limit).results;
    }
    // Lookup a single Episode
    suspend fun lookupEpisode(id: String) : ITunesAPI.Episode {
        return iTunesAPI.lookupEpisode(id).results[0];
    }

    // Get the list of episodes by a specific artist
    suspend fun getArtistEpisodes(id: String, limit: Int) : List<ITunesAPI.Episode> {
        return iTunesAPI.getArtistEpisodes(id, limit).results;
    }

    // Get the first episode for a Podcast Artist and get its image URL
    suspend fun getPodcastArtistImage(artistName: String) : String? {
        val result = iTunesAPI.getPodcastArtistImage(artistName.htmlEncode(), 1).results;
        if (result.isNotEmpty()) return result[0].artworkUrl100.replace("100x100", "600x600");
        else return null;
    }

}
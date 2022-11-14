package com.cs371m.mypod.ui

import android.util.Log
import androidx.core.text.htmlEncode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cs371m.mypod.api.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Dunno what this does
    private var navController:NavController?= null;

    // API Stuff
    private val iTunesAPI = ITunesAPI.create();
    private val myPodRepo = MyPodRepo(iTunesAPI);

    // Search results
    private val podcastArtistSearchResults = MutableLiveData<List<ITunesAPI.PodcastArtist>>()
    private val episodeSearchResults = MutableLiveData<List<ITunesAPI.Episode>>();

    // Subscriptions and Continue Listening Lists
    private val subscriptionList = MutableLiveData<List<String>>();
    private val subscriptionListData = MutableLiveData<List<ITunesAPI.PodcastArtist>>();
    private val continueList = MutableLiveData<List<String>>();
    private val continueListListData = MutableLiveData<List<ITunesAPI.Episode>>();



    // Podcast Search using a search term
    fun searchPodcastArtists(term: String, limit: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
                    + CoroutineExceptionHandler{
                        _, throwable ->
                            Log.d("#################################################", "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}")
                            throwable.printStackTrace();
                    }
        ) {
            // Get search results
            val result = myPodRepo.searchPodcastArtists(term, limit);
            // Get the images for search results
            if (result.isNotEmpty()) {
                for (i in 0..(result.size - 1)) {
                    result[i].imageUrl = getPodcastArtistImage(result[i].artistName);
                }
            }
            podcastArtistSearchResults.postValue(result);
        }
    }

    // Episode Search using a search term
    fun searchEpisodes(term: String, limit: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
                    + CoroutineExceptionHandler{
                        _, throwable ->
                        Log.d("#################################################", "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}")
                        throwable.printStackTrace();
                    }
        ) {
            // Get search results
            val result = myPodRepo.searchEpisodes(term, limit);
            for (i in 0..(result.size - 1)) {
                if (result[i].artworkUrl600 == null) result[i].artworkUrl600 = "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
            }
            episodeSearchResults.postValue(result);
        }
    }

    // Get data for the subscription list
    fun processSubscriptionList() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

        val ids = subscriptionList.value;
        if (ids != null) {
            val podcastList = mutableListOf<ITunesAPI.PodcastArtist>();
            // Get lookup data and append image to each podcast
            for (i in 0..(ids.size - 1)) {
                val podcast = myPodRepo.lookupPodcastArtist(ids[i]);
                podcast.imageUrl = getPodcastArtistImage(podcast.artistName);
                podcastList.add(podcast);
            }
            subscriptionListData.postValue(podcastList);
        }
    }

    // Get data for the continue listening list
    fun processContinueList() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

        val ids = continueList.value;
        if (ids != null) {
            val episodeList = mutableListOf<ITunesAPI.Episode>();
            // Get lookup data and append image to each podcast
            for (i in 0..(ids.size - 1)) episodeList.add(myPodRepo.lookupEpisode(ids[i]));
            continueListListData.postValue(episodeList);
        }
    }

    // Get image for Podcast Artists
    suspend fun getPodcastArtistImage(name: String): String {
//        val image = myPodRepo.getPodcastArtistImage(name);
//        if (image != null) return image;
//        else return "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
        return "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
    }

    // Observers
    fun observePodcastArtistSearchResults(): LiveData<List<ITunesAPI.PodcastArtist>> {return podcastArtistSearchResults};
    fun observeEpisodeSearchResults(): LiveData<List<ITunesAPI.Episode>> {return episodeSearchResults};
    fun observeSubscriptionList(): LiveData<List<String>> {return subscriptionList};
    fun observeSubscriptionListData(): LiveData<List<ITunesAPI.PodcastArtist>> {return subscriptionListData};
    fun observeContinueList(): LiveData<List<String>> {return continueList};
    fun observeContinueListData(): LiveData<List<ITunesAPI.Episode>> {return continueListListData};

    // Setters
    fun setSubscriptionList(list: List<String>) {
        subscriptionList.postValue(list);
    }
    fun setContinueList(list: List<String>) {
        continueList.postValue(list);
    }

    // Weird Nav Controller Stuff
    fun setNavController(navController: NavController){
        this.navController = navController
    }
    fun getNavController():NavController{
        return navController!!
    }

}
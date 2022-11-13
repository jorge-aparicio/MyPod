package com.cs371m.mypod.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs371m.mypod.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val podchaserApi = PodchaserAPI.create();
    private val myPodRepo = MyPodRepo(podchaserApi);
    private val searchResults = MutableLiveData<List<PodcastSearchQuery.Data1>>();
    private val podcastsList = MutableLiveData<List<String>>();
    private val podcastsDataList = MutableLiveData<List<PodcastQuery.Podcast>>();

    // profile values
    private val podcastProfile = MutableLiveData<PodcastQuery.Podcast>();
    private val profileEpisodes = MutableLiveData<List<EpisodesQuery.Data1>>();
    private val episodesPageInfo = MutableLiveData<EpisodesQuery.PaginatorInfo>();

    // Podcast Search using a search term
    fun searchPodcasts(term: String, limit: Int, page: Int) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        val result = myPodRepo.podcastSearch(term, limit,page);
        if (result != null) {
            val list = mutableListOf<PodcastSearchQuery.Data1>();
            for (i in 0..result.data.size - 1) if (result.data[i].applePodcastsId != null) list.add(result.data[i]);
            searchResults.postValue(list)
        }
    }

    // Get podcast data for each id in list
    fun searchPodcastsList() {
        val list = getPodcastsList();
        for (i in 0..list.size - 1) {
            searchPodcastData(list[i])
        }
    }

    // Data for a single podcast
    private fun searchPodcastData(id: String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

        // Get Podcast Data
        val result = myPodRepo.getPodcast(id);

        // Append podcast to list
        val list = getPodcastsDataList().toMutableList();
        if (result != null) list.add(result);

        // Post to live data
        if (list.isNotEmpty()) podcastsDataList.postValue(list);
    }

    fun updateProfile(id: String)= viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

        // Get Podcast Data
        val result = myPodRepo.getPodcast(id);

        // set profile podcast
        if (result != null) podcastProfile.postValue(result!!)

        val episodesResult = myPodRepo.getEpisodes(id,0);
        if(episodesResult!=null) {
            episodesPageInfo.postValue(episodesResult!!.paginatorInfo!!)
            profileEpisodes.postValue(episodesResult!!.data)}

    }

    fun getMoreEpisodes(id:String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        val currentPage = episodesPageInfo.value!!.currentPage
        if(episodesPageInfo.value!!.hasMorePages) {
            val episodesResult = myPodRepo.getEpisodes(id, (currentPage+1));
            if (episodesResult != null) {
                val list = profileEpisodes.value
                episodesPageInfo.postValue(episodesResult!!.paginatorInfo!!)
                profileEpisodes.postValue(episodesResult!!.data)
            }
        }
    }


    // Observers
    fun observeSearchResults(): LiveData<List<PodcastSearchQuery.Data1>> {return searchResults; }
    fun observePodcastsList(): LiveData<List<String>> {return podcastsList; }
    fun observePodcastsDataList(): LiveData<List<PodcastQuery.Podcast>> {return podcastsDataList; }


    // profile observers
    fun observePodcastProfile():LiveData<PodcastQuery.Podcast>{return podcastProfile}
    fun observeProfileEpisodes():LiveData<List<EpisodesQuery.Data1>>{return profileEpisodes}

    // Helper Getters
    private fun getPodcastsList() : List<String> {
        if (podcastsList.value != null) return podcastsList.value!!;
        else return List<String>(0) {""};
    }

    private fun getPodcastsDataList() : List<PodcastQuery.Podcast> {
        if (podcastsDataList.value != null) return podcastsDataList.value!!;
        else return List<PodcastQuery.Podcast>(0) {PodcastQuery.Podcast("", "", "",null,"")};
    }

    // Setters
    fun setPodcastsList(list: List<String>) {
        podcastsList.postValue(list);
    }

    fun debugPL(): List<String> {
        return podcastsList.value!!;
    }

}
package com.cs371m.mypod.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs371m.mypod.api.MyPodRepo
import com.cs371m.mypod.api.PodcastSearchQuery
import com.cs371m.mypod.api.PodchaserAPI
import com.cs371m.mypod.api.ProfileQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val podchaserApi = PodchaserAPI.create();
    private val myPodRepo = MyPodRepo(podchaserApi);
    private val searchResults = MutableLiveData<List<PodcastSearchQuery.Data1>>();
    private val podcastsList = MutableLiveData<List<String>>();
    private val podcastsDataList = MutableLiveData<List<ProfileQuery.Podcast>>();

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
        val result = myPodRepo.getProfile(id);

        // Append podcast to list
        val list = getPodcastsDataList().toMutableList();
        if (result != null) list.add(result);

        // Post to live data
        if (list.isNotEmpty()) podcastsDataList.postValue(list);
    }

    // Observers
    fun observeSearchResults(): LiveData<List<PodcastSearchQuery.Data1>> {return searchResults; }
    fun observePodcastsList(): LiveData<List<String>> {return podcastsList; }
    fun observePodcastsDataList(): LiveData<List<ProfileQuery.Podcast>> {return podcastsDataList; }

    // Helper Getters
    private fun getPodcastsList() : List<String> {
        if (podcastsList.value != null) return podcastsList.value!!;
        else return List<String>(0) {""};
    }

    private fun getPodcastsDataList() : List<ProfileQuery.Podcast> {
        if (podcastsDataList.value != null) return podcastsDataList.value!!;
        else return List<ProfileQuery.Podcast>(0) {ProfileQuery.Podcast("", "", "",null,null,null,null)};
    }

    // Setters
    fun setPodcastsList(list: List<String>) {
        podcastsList.postValue(list);
    }

    fun debugPL(): List<String> {
        return podcastsList.value!!;
    }

}
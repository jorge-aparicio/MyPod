package com.cs371m.mypod.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs371m.mypod.api.MyPodRepo
import com.cs371m.mypod.api.PodcastSearchQuery
import com.cs371m.mypod.api.PodchaserAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val podchaserApi = PodchaserAPI.create();
    private val myPodRepo = MyPodRepo(podchaserApi);
    private val searchResults = MutableLiveData<PodcastSearchQuery.Data>();

    //SSS
    fun searchPodcasts(term: String, limit: Int) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        searchResults.postValue(myPodRepo.podcastSearch(term, limit).data!!)
    }

    fun observeSearchResults(): LiveData<PodcastSearchQuery.Data> {
        return searchResults;
    }


}
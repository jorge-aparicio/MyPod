package com.cs371m.mypod.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cs371m.mypod.api.AppleAPI
import com.cs371m.mypod.api.ITunesAPI
import com.cs371m.mypod.api.MyPodRepo
import com.cs371m.mypod.models.PodcastTypes
import com.cs371m.mypod.xml.FeedDownloader
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel : ViewModel() {

    // Dunno what this does
    private var navController: NavController? = null;

    // API Stuff
    private val iTunesAPI = ITunesAPI.create();
    private val appleAPI = AppleAPI.create();
    private val myPodRepo = MyPodRepo(iTunesAPI, appleAPI);

    // Search results
    private val podcastSearchResults = MutableLiveData<List<ITunesAPI.Podcast>>()

    // Subscriptions and Continue Listening Lists
    private val subscriptionList = MutableLiveData<List<String>>();
    private val subscriptionListData = MutableLiveData<List<ITunesAPI.Podcast>>();
    private val continueList = MutableLiveData<List<String>>();
    private val continueListListData = MutableLiveData<List<ITunesAPI.Podcast>>();

    //
    private val podcastProfile = MutableLiveData<PodcastTypes.PodcastProfile>();
    private val profileEpisodes = MutableLiveData<List<PodcastTypes.PodcastEpisode>>();
    private val lastEpisodeIndex = MutableLiveData(15);
    private val episodeIncrement = 15;

    fun getTop25() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
                    + CoroutineExceptionHandler { _, throwable ->
                Log.d(
                    "#################################################",
                    "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}"
                )
                throwable.printStackTrace();
            }
        ) {
            // Get search results
            val appleApiList = myPodRepo.getTop25()
            val result = appleApiList.map { applePod ->
                ITunesAPI.Podcast(
                    applePod.id,
                    applePod.name,
                    applePod.artworkUrl100
                )
            }.toList()
            // Get the images for search result
            podcastSearchResults.postValue(result);
        }
    }

    // Podcast Search using a search term
    fun searchPodcasts(term: String, limit: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
                    + CoroutineExceptionHandler { _, throwable ->
                Log.d(
                    "#################################################",
                    "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}"
                )
                throwable.printStackTrace();
            }
        ) {
            // Get search results
            val result = myPodRepo.searchPodcasts(term, limit);
            // Get the images for search result
            podcastSearchResults.postValue(result);
        }
    }

    fun updateProfile(id: String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {

        // Get Podcast Data
        val podcast = myPodRepo.lookupPodcast(id)
        val feedUrl = podcast.feedUrl
        val imageUrl = podcast.artworkUrl100
//        val channel = myPodRepo.getChannel(feedUrl)
        withContext(Dispatchers.Default) {
            val channel = FeedDownloader().loadXmlFromNetwork(feedUrl)[0]
            println(channel)

            // set profile podcast
            println(channel)
            if (channel != null) podcastProfile.postValue(
                PodcastTypes.PodcastProfile(
                    id,
                    podcast.collectionName,
                    imageUrl,
                    feedUrl,
                    channel.description!!,
                    channel.items!!.size
                )
            )
            val channelEps = channel.items
            lastEpisodeIndex.postValue(15);
            val episodes = channelEps!!.map { article ->
                 val uuidString= article.title!! + channel.title
                val uuid = UUID.nameUUIDFromBytes(uuidString.toByteArray());
                PodcastTypes.PodcastEpisode(
                    uuid.toString(),
                    article.title!!,
                    article.audioUrl!!,
                    article.image,
                    article.pubDate,
                    convertTime(article.duration)
                )
            }.toList()
            if (episodes.isNotEmpty()) {
                profileEpisodes.postValue(episodes)
            }

        }

    }

//    fun getMoreEpisodes(feed: String, inc: Int) = viewModelScope.launch(
//        context = viewModelScope.coroutineContext
//                + Dispatchers.IO
//    ) {
//        val channel = parser.getChannel(feed)
//        val channelEps = channel.articles
//        lastEpisodeIndex.postValue(lastEpisodeIndex.value!!+episodeIncrement);
//        val episodes = channelEps.filterIndexed { index, _ ->
//            if (index < lastEpisodeIndex.value!! && index > (lastEpisodeIndex.value!! - episodeIncrement)) true
//            false
//        }.map { article ->
//            PodcastTypes.PodcastEpisode(
//                article.guid!!,
//                article.title!!,
//                article.audio!!,
//                article.image!!,
//                article.pubDate!!
//            )
//        }.toList()
//
//        if (episodes.isNotEmpty()) {
//            profileEpisodes.postValue(episodes)
//        }
//    }

//    // Episode Search using a search term
//    fun searchEpisodes(term: String, limit: Int) {
//        viewModelScope.launch(
//            context = viewModelScope.coroutineContext
//                    + Dispatchers.IO
//                    + CoroutineExceptionHandler{
//                        _, throwable ->
//                        Log.d("#################################################", "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}")
//                        throwable.printStackTrace();
//                    }
//        ) {
//            // Get search results
//            val result = myPodRepo.searchEpisodes(term, limit);
//            for (i in 0..(result.size - 1)) {
//                if (result[i].artworkUrl600 == null) result[i].artworkUrl600 = "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
//            }
//            episodeSearchResults.postValue(result);
//        }
//    }

    // Get data for the subscription list
    fun processSubscriptionList() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

//        val ids = subscriptionList.value;
//        if (ids != null) {
//            val podcastList = mutableListOf<ITunesAPI.PodcastArtist>();
//            // Get lookup data and append image to each podcast
//            for (i in 0..(ids.size - 1)) {
//                val podcast = myPodRepo.lookupPodcastArtist(ids[i]);
//                podcast.imageUrl = getPodcastArtistImage(podcast.artistName);
//                podcastList.add(podcast);
//            }
//            subscriptionListData.postValue(podcastList);
//        }
    }

    // Get data for the continue listening list
    fun processContinueList() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {

//        val ids = continueList.value;
//        if (ids != null) {
//            val episodeList = mutableListOf<ITunesAPI.Episode>();
//            // Get lookup data and append image to each podcast
//            for (i in 0..(ids.size - 1)) episodeList.add(myPodRepo.lookupEpisode(ids[i]));
//            continueListListData.postValue(episodeList);
//        }
    }

    // Get image for Podcast Artists
    suspend fun getPodcastArtistImage(name: String): String {
//        val image = myPodRepo.getPodcastArtistImage(name);
//        if (image != null) return image;
//        else return "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
        return "https://upload.wikimedia.org/wikipedia/commons/f/f1/Heavy_red_%22x%22.png";
    }


    // Observers
    fun observePodcastArtistSearchResults(): LiveData<List<ITunesAPI.Podcast>> {return podcastSearchResults};
//    fun observeEpisodeSearchResults(): LiveData<List<ITunesAPI.Episode>> {return episodeSearchResults};
    fun observeSubscriptionList(): LiveData<List<String>> {return subscriptionList};
    fun observeSubscriptionListData(): LiveData<List<ITunesAPI.Podcast>> {return subscriptionListData};
    fun observeContinueList(): LiveData<List<String>> {return continueList};
    fun observeContinueListData(): LiveData<List<ITunesAPI.Podcast>> {return continueListListData};

    //
    fun observePodcastProfile():MutableLiveData<PodcastTypes.PodcastProfile>{
        return podcastProfile
    }
    fun observeProfileEpisodes():MutableLiveData<List<PodcastTypes.PodcastEpisode>>{
        return profileEpisodes
    }

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
    // This method converts time in milliseconds to minutes-second formatted string
    private fun convertTime(duration: String?): String {
        //XXX Write me
        if(duration!= null){
        try{
            val seconds = duration.toInt()
        val totalMinutes = seconds / 60
        val remainingSeconds = seconds % 60

        return if (totalMinutes < 60 )
            String.format("%02d:%02d", totalMinutes, remainingSeconds)
        else{
            val totalHours = totalMinutes/60
            val remainingMinutes = totalMinutes % 60
            String.format("%02d:%02d:%02d", totalHours,remainingMinutes, remainingSeconds)

        }
        }catch(e:Exception){
            return duration
        }
    }
        return "??:??:??"
    }

}
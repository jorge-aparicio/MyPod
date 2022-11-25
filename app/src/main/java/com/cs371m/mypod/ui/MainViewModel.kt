package com.cs371m.mypod.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.cs371m.mypod.api.AppleAPI
import com.cs371m.mypod.api.ITunesAPI
import com.cs371m.mypod.api.MyPodRepo
import com.cs371m.mypod.db.EpisodeDao
import com.cs371m.mypod.db.MyPodDatabase
import com.cs371m.mypod.db.MyPodDbRepo
import com.cs371m.mypod.db.PodcastDao
import com.cs371m.mypod.xml.FeedDownloader
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel(
    application: Application):
AndroidViewModel(application) {

    // Dunno what this does
    private var navController: NavController? = null;

    // API Stuff
    private val iTunesAPI = ITunesAPI.create();
    private val appleAPI = AppleAPI.create();
    private val myPodRepo = MyPodRepo(iTunesAPI, appleAPI);

    // Search results
    private val podcastSearchResults = MutableLiveData<List<ITunesAPI.Podcast>>()

    // Subscriptions and Continue Listening Lists
    private val continueList = MutableLiveData<List<String>>();
    private val continueListListData = MutableLiveData<List<ITunesAPI.Podcast>>();

    //
    private val podcastProfile = MutableLiveData<PodcastDao.Podcast>();
    private val profileEpisodes = MutableLiveData<List<EpisodeDao.Episode>>();
    private val lastEpisodeIndex = MutableLiveData(15);
    private var db: MyPodDatabase = MyPodDatabase.getDatabase(getApplication<Application>().applicationContext);
    private val myPodDbRepo = MyPodDbRepo(db)

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
                    applePod.artworkUrl100,
                    null
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
            podcastSearchResults.postValue(result.filter { podcast -> podcast.feedUrl != null });
        }
    }

    fun updateProfile(id: Int) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {

        // Get Podcast Data
        //
        val dbPodcast = myPodDbRepo.getPodcast(id)
        val subscribed =  if(dbPodcast != null){
                println("!!!LOADING FROM DB!!!")
                var eps = myPodDbRepo.loadEpisodesByPodcastId(id)
                podcastProfile.postValue(dbPodcast!!)
                profileEpisodes.postValue(eps!!)
                dbPodcast.subscribed
                } else false
            val podcast = myPodRepo.lookupPodcast(id.toString())
            val feedUrl = podcast.feedUrl!!
            val imageUrl = podcast.artworkUrl100
            withContext(Dispatchers.Default) {
                val channel = FeedDownloader().loadXmlFromNetwork(feedUrl)[0]

                // set profile podcast
                if (channel != null) {
                    val newPod =  PodcastDao.Podcast(
                            id,
                    podcast.collectionName,
                    imageUrl,
                    feedUrl,
                    channel.description!!,
                    channel.items!!.size,
                        subscribed
                    )
                    podcastProfile.postValue(
                        newPod
                    )
                    myPodDbRepo.insertPodcast(newPod)
                    val channelEps = channel.items
                    val episodes = channelEps!!.filter { article -> article.audioUrl != null }
                        .mapIndexed { index, article ->
                            val uuidString = article.title!! + channel.title
                            val uuid = UUID.nameUUIDFromBytes(uuidString.toByteArray());
                            val image = article.image ?: imageUrl
                            EpisodeDao.Episode(
                                uuid.toString(),
                                article.title!!,
                                article.audioUrl!!,
                                image,
                                article.pubDate,
                                convertTime(article.duration),
                                id,
                                channel.items.size - index
                            )
                        }.toList()
                    if (episodes.isNotEmpty()) {
                        profileEpisodes.postValue(episodes)
                        episodes.forEach { episode -> myPodDbRepo.insertEpisode(episode) }

                    }
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
    fun observeSubscriptionList(): LiveData<List<PodcastDao.Podcast>> {return myPodDbRepo.loadSubscriptions()};
    fun observeContinueList(): LiveData<List<String>> {return continueList};
    fun observeContinueListData(): LiveData<List<ITunesAPI.Podcast>> {return continueListListData};

    //
    fun observePodcastProfile():MutableLiveData<PodcastDao.Podcast>{
        return podcastProfile
    }
    fun observeProfileEpisodes():MutableLiveData<List<EpisodeDao.Episode>>{
        return profileEpisodes
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

    fun getDb():MyPodDbRepo{
        return myPodDbRepo!!
    }
    fun toggleSubscribed()= viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalPod = podcastProfile.value!!
        val newPod = PodcastDao.Podcast(
            originalPod!!.id,
            originalPod!!.title,
            originalPod!!.imageUrl,
            originalPod!!.feedUrl,
            originalPod!!.description,
            originalPod!!.numEpisodes,
            !originalPod!!.subscribed
        )
//        podcastProfile.postValue(newPod)
        myPodDbRepo.updatePodcast(newPod)
        podcastProfile.postValue(newPod)
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
package com.cs371m.mypod.ui

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cs371m.mypod.api.MyPodRepo
import com.cs371m.mypod.databinding.ProfileRowBinding
import com.cs371m.mypod.db.EpisodeDao
import com.cs371m.mypod.db.MyPodDatabase
import com.cs371m.mypod.db.MyPodDbRepo
import com.cs371m.mypod.db.PodcastDao
import com.cs371m.mypod.xml.FeedDownloader
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


class MainViewModel(
    application: Application
) :
    AndroidViewModel(application) {

    // Dunno what this does
    private var navController: NavController? = null

    // API Stuff
    private val iTunesAPI = ITunesAPI.create()
    private val appleAPI = AppleAPI.create()
    private val myPodRepo = MyPodRepo(iTunesAPI, appleAPI)

    //db stuff
    private var db: MyPodDatabase =
        MyPodDatabase.getDatabase(getApplication<Application>().applicationContext)
    private val myPodDbRepo = MyPodDbRepo(db)

    // Search results
    private val podcastSearchResults = MutableLiveData<List<ITunesAPI.Podcast>>()

    // podcast profile
    private val podcastProfile = MutableLiveData<PodcastDao.Podcast>()
    private val profileEpisodes = MutableLiveData<List<EpisodeDao.Episode>>()

    // Media Player Stuff
    private val currPlaying = MutableLiveData<EpisodeDao.Episode>()
    private var downloadFunc: ((EpisodeDao.Episode) -> Unit)? = null

    fun getTop25() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
                    + CoroutineExceptionHandler { _, throwable ->
                Log.d(
                    "#################################################",
                    "ERROR!!!!!!!!!!!!!!!!!!!!!!!: {$throwable.message}"
                )
                throwable.printStackTrace()
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
            podcastSearchResults.postValue(result)
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
                throwable.printStackTrace()
            }
        ) {
            // Get search results
            val result = myPodRepo.searchPodcasts(term, limit)

            // Get the images for search result
            podcastSearchResults.postValue(result.filter { podcast -> podcast.feedUrl != null })
        }
    }

    fun updateProfile(id: Int) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {

        // Get Podcast Data
        //
        val dbPodcast = myPodDbRepo.getPodcast(id)
        val subscribed = if (dbPodcast != null) {
            println("!!!LOADING FROM DB!!!")
            var eps = myPodDbRepo.loadEpisodesByPodcastId(id)
            podcastProfile.postValue(dbPodcast!!)
            profileEpisodes.postValue(eps)
            dbPodcast.subscribed
        } else false
        val podcast = myPodRepo.lookupPodcast(id.toString())
        val feedUrl = podcast.feedUrl!!
        val imageUrl = podcast.artworkUrl100
        withContext(Dispatchers.Default) {
            val channel = FeedDownloader().loadXmlFromNetwork(feedUrl)[0]

            // set profile podcast
            if (channel != null) {
                val newPod = PodcastDao.Podcast(
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
                val episodes = channelEps.filter { article -> article.audioUrl != null }
                    .mapIndexed { index, article ->
                        val uuidString = article.title!! + channel.title
                        val uuid = UUID.nameUUIDFromBytes(uuidString.toByteArray())
                        val episode = myPodDbRepo.getEpisodeById(uuid.toString())
                        if(episode != null){
                            episode
                        }
                        else{
                        val image = article.image ?: imageUrl
                        EpisodeDao.Episode(
                            uuid.toString(),
                            article.title,
                            article.audioUrl!!,
                            image,
                            article.pubDate,
                            convertTime(article.duration),
                            id,
                            podcast.collectionName,
                            channel.items.size - index
                        )}
                    }.toList()
                if (episodes.isNotEmpty()) {
                    profileEpisodes.postValue(episodes)
                    episodes.forEach { episode -> myPodDbRepo.insertEpisode(episode) }

                }
            }
        }
    }


    // Observers
    fun observePodcastArtistSearchResults(): LiveData<List<ITunesAPI.Podcast>> {
        return podcastSearchResults
    }

    fun observeSubscriptionList(): LiveData<List<PodcastDao.Podcast>> {
        return myPodDbRepo.loadSubscriptions()
    }

    fun observeContinueList(): LiveData<List<EpisodeDao.Episode>> {
        return myPodDbRepo.getUnfinished()
    }

    fun observeNewEpsList(): LiveData<List<EpisodeDao.Episode>> {
        return myPodDbRepo.getLatestEpisodes()
    }

    fun observePodcastProfile(): MutableLiveData<PodcastDao.Podcast> {
        return podcastProfile
    }

    fun observeProfileEpisodes(): MutableLiveData<List<EpisodeDao.Episode>> {
        return profileEpisodes
    }

    fun observeCurrPlaying(): MutableLiveData<EpisodeDao.Episode> {
        return currPlaying
    }

    // Weird Nav Controller Stuff
    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun getNavController(): NavController {
        return navController!!
    }

    fun getDb(): MyPodDbRepo {
        return myPodDbRepo
    }

    fun toggleSubscribed() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalPod = podcastProfile.value!!
        val newPod = PodcastDao.Podcast(
            originalPod.id,
            originalPod.title,
            originalPod.imageUrl,
            originalPod.feedUrl,
            originalPod.description,
            originalPod.numEpisodes,
            !originalPod.subscribed
        )
        myPodDbRepo.updatePodcast(newPod)
        podcastProfile.postValue(newPod)
    }

    private fun convertTime(duration: String?): String {
        if (duration != null) {
            try {
                val seconds = duration.toInt()
                val totalMinutes = seconds / 60
                val remainingSeconds = seconds % 60

                return if (totalMinutes < 60)
                    String.format("%02d:%02d", totalMinutes, remainingSeconds)
                else {
                    val totalHours = totalMinutes / 60
                    val remainingMinutes = totalMinutes % 60
                    String.format("%02d:%02d:%02d", totalHours, remainingMinutes, remainingSeconds)

                }
            } catch (e: Exception) {
                return duration
            }
        }
        return "??:??:??"
    }

   // episode mod functions
   fun setStarted(id:String,started:Boolean) = viewModelScope.launch(
       context = viewModelScope.coroutineContext
               + Dispatchers.IO
   ) {
       val originalEp = myPodDbRepo.getEpisodeById(id)
       val newEp = EpisodeDao.Episode(
           originalEp.id,
           originalEp.title,
           originalEp.audioUrl,
           originalEp.imageUrl!!,
           originalEp.pubDate,
           originalEp.duration,
           originalEp.podcastId,
           originalEp.podcastName,
           originalEp.episodeNumber,
           started,
           originalEp.progress,
           originalEp.played,
           originalEp.downloadId,
           originalEp.audioPath,
           originalEp.downloaded
       )
       myPodDbRepo.updateEpisode(newEp)

   }

    fun setDownloaded(id:Int,downloaded:Boolean) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalEp = myPodDbRepo.getEpisodeByDownloadId(id)
        if(originalEp != null) {
            val newEp = EpisodeDao.Episode(
                originalEp.id,
                originalEp.title,
                originalEp.audioUrl,
                originalEp.imageUrl!!,
                originalEp.pubDate,
                originalEp.duration,
                originalEp.podcastId,
                originalEp.podcastName,
                originalEp.episodeNumber,
                originalEp.started,
                originalEp.progress,
                originalEp.played,
                originalEp.downloadId,
                originalEp.audioPath,
                downloaded
            )
            myPodDbRepo.updateEpisode(newEp)
        }
    }

    fun setDownloadInfo(id:String,downloadID: Int, audioPath:String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalEp = myPodDbRepo.getEpisodeById(id)
        val newEp = EpisodeDao.Episode(
            originalEp.id,
            originalEp.title,
            originalEp.audioUrl,
            originalEp.imageUrl!!,
            originalEp.pubDate,
            originalEp.duration,
            originalEp.podcastId,
            originalEp.podcastName,
            originalEp.episodeNumber,
            originalEp.started,
            originalEp.progress,
            originalEp.played,
            downloadID,
            audioPath,
            originalEp.downloaded
        )
        myPodDbRepo.updateEpisode(newEp)

    }

    fun setPlayed(id:String,played:Boolean) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalEp = myPodDbRepo.getEpisodeById(id)
        val newEp = EpisodeDao.Episode(
            originalEp.id,
            originalEp.title,
            originalEp.audioUrl,
            originalEp.imageUrl!!,
            originalEp.pubDate,
            originalEp.duration,
            originalEp.podcastId,
            originalEp.podcastName,
            originalEp.episodeNumber,
            false,
            0,
            played,
            originalEp.downloadId,
            originalEp.audioPath,
            originalEp.downloaded
        )
        myPodDbRepo.updateEpisode(newEp)

    }

    fun setProgress(id:String,progress:Int) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO
    ) {
        val originalEp = myPodDbRepo.getEpisodeById(id)
        val newEp = EpisodeDao.Episode(
            originalEp.id,
            originalEp.title,
            originalEp.audioUrl,
            originalEp.imageUrl!!,
            originalEp.pubDate,
            originalEp.duration,
            originalEp.podcastId,
            originalEp.podcastName,
            originalEp.episodeNumber,
            originalEp.started,
            progress,
            originalEp.played,
            originalEp.downloadId,
            originalEp.audioPath,
            originalEp.downloaded
        )
        myPodDbRepo.updateEpisode(newEp)

    }

    fun setCurrPlaying(episode: EpisodeDao.Episode) {
        currPlaying.postValue(episode)
    }

    fun setDownloadFun(func: (EpisodeDao.Episode)->Unit){
        downloadFunc = func
    }

    fun downloadEpisode(episode: EpisodeDao.Episode,downloadID: Int, file:String) = viewModelScope.launch(
    context = viewModelScope.coroutineContext
    + Dispatchers.IO
    ) {

        setDownloadInfo(episode.id,downloadID, file)
    }


    fun showBottomSheetDialog(context:Context, episode: EpisodeDao.Episode):Boolean {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(com.cs371m.mypod.R.layout.bottom_sheet)
        val play = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.playLinearLayout)
        play?.setOnClickListener {
            setCurrPlaying(episode)
            bottomSheetDialog.dismiss()
        }
        val markPlayed = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.markLinearLayout)
        markPlayed?.setOnClickListener {
            setPlayed(episode.id, !episode.played)
            bottomSheetDialog.dismiss()
        }
        val download = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.downloadLinearLayout)
        download?.setOnClickListener {
            downloadFunc?.let { it1 -> it1(episode) }
            bottomSheetDialog.dismiss()
        }
        val share = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.shareLinearLayout)
        share?.setOnClickListener {
            bottomSheetDialog.dismiss()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, episode.audioUrl)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(context,shareIntent,null)

        }
        bottomSheetDialog.show()
        return true
    }

    fun showBottomDialogProfile(context:Context, episode: EpisodeDao.Episode, rowBinding:ProfileRowBinding):Boolean{
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(com.cs371m.mypod.R.layout.bottom_sheet)
        val play = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.playLinearLayout)
        play?.setOnClickListener {
            setCurrPlaying(episode)
            bottomSheetDialog.dismiss()
        }
        val markPlayed = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.markLinearLayout)
        markPlayed?.setOnClickListener {
            setPlayed(episode.id, !episode.played)
            if(!episode.played){
                rowBinding.episodeTitle.setTextColor(Color.GRAY)
                rowBinding.episodeTime.setTextColor(Color.GRAY)
                rowBinding.episodeDate.setTextColor(Color.GRAY)

            }else{
                rowBinding.episodeTitle.setTextColor(Color.WHITE)
                rowBinding.episodeTime.setTextColor(Color.WHITE)
                rowBinding.episodeDate.setTextColor(Color.WHITE)
            }
            bottomSheetDialog.dismiss()

        }
        val download = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.downloadLinearLayout)
        download?.setOnClickListener {
            downloadFunc?.let { it1 -> it1(episode) }
            bottomSheetDialog.dismiss()
        }
        val share = bottomSheetDialog.findViewById<LinearLayout>(com.cs371m.mypod.R.id.shareLinearLayout)
        share?.setOnClickListener {
            bottomSheetDialog.dismiss()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, episode.audioUrl)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(context,shareIntent,null)

        }
        bottomSheetDialog.show()
        return true


    // This method converts time in milliseconds to minutes-second formatted string
    fun convertTime(seconds: Int): String {
        //XXX Write me
        val totalMinutes = seconds / 60
        val remainingSeconds = seconds % 60

        return if (totalMinutes < 60 )
            String.format("%02d:%02d", totalMinutes, remainingSeconds)
        else{
            val totalHours = totalMinutes/60
            val remainingMinutes = totalMinutes % 60
            String.format("%02d:%02d:%02d", totalHours,remainingMinutes, remainingSeconds)

        }

    }
}
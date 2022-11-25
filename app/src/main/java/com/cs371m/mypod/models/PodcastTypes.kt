package com.cs371m.mypod.models

import android.app.ActivityManager.TaskDescription
import java.time.Duration

interface PodcastTypes {
    data class Podcast(val collectionId: String,
                       val collectionName: String,
                       val artworkUrl100: String,
    )

    data class PodcastProfile(val collectionId: String,
                           val collectionName: String,
                           val artworkUrl100: String,
                           val feedUrl:String,
                           val description: String,
                           val numEpisodes: Int

    )
    data class PodcastEpisode(val id:String,
                              val episodeName: String,
                              val audioUrl: String,
                              val artworkUrl: String?,
                              val pubDate: String?,
                              val duration: String?,
                              val number: Int?
                              )
}
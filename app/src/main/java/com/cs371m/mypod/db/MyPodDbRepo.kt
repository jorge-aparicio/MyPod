package com.cs371m.mypod.db

import androidx.lifecycle.LiveData
import androidx.room.*

class MyPodDbRepo(private val db:MyPodDatabase) {

    suspend fun insertPodcast(podcast: PodcastDao.Podcast){
        db.podcastDao().insertPodcast(podcast)
    }

    suspend fun updatePodcast(podcast: PodcastDao.Podcast){
        db.podcastDao().updatePodcast(podcast)
    }

    suspend fun deletePodcast(podcast: PodcastDao.Podcast){
        db.podcastDao().deletePodcast(podcast)
    }

    fun getPodcast(id:Int): PodcastDao.Podcast{
        return db.podcastDao().getPodcast(id)
    }

    fun loadPodcastById(id: Int): LiveData<PodcastDao.Podcast>{
        return db.podcastDao().loadPodcastById(id)
    }

    fun loadSubscriptions(): LiveData<List<PodcastDao.Podcast>>{
       return db.podcastDao().loadSubscriptions()
    }

    suspend fun insertEpisode(episode: EpisodeDao.Episode){
        db.episodeDao().insertEpisode(episode)
    }

    suspend fun updateEpisode(episode: EpisodeDao.Episode){
        db.episodeDao().updateEpisode(episode)
    }

    suspend fun deleteEpisode(episode: EpisodeDao.Episode){
        db.episodeDao().deleteEpisode(episode)
    }

    fun getEpisodeById(id: String): LiveData<EpisodeDao.Episode>{
        return db.episodeDao().getEpisodeById(id)
    }

    fun loadEpisodesByPodcastId(podcastId: Int): LiveData<List<EpisodeDao.Episode>>{
        return db.episodeDao().loadEpisodesByPodcastId(podcastId)
    }


    fun getLatestEpisodes(): LiveData<List<EpisodeDao.Episode>>{
        return db.episodeDao().getLatestEpisodes()
    }

    fun getUnfinished(): LiveData<List<EpisodeDao.Episode>>{
        return db.episodeDao().getUnfinished()
    }
}
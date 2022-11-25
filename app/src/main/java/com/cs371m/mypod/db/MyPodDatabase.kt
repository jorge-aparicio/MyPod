package com.cs371m.mypod.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PodcastDao.Podcast::class, EpisodeDao.Episode::class], version = 1)
abstract class MyPodDatabase: RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
}
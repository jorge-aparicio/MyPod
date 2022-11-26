package com.cs371m.mypod.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PodcastDao.Podcast::class, EpisodeDao.Episode::class, EpisodeDao.EpisodeDownload::class], version = 1)
abstract class MyPodDatabase: RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao


    companion object {

        @Volatile
        private var INSTANCE: MyPodDatabase? = null

        private const val DB_NAME = "mypod.db"

        fun getDatabase(context: Context): MyPodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyPodDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
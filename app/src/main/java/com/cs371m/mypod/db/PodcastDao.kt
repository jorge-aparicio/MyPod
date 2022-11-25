package com.cs371m.mypod.db

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface PodcastDao {
        @Entity(tableName = "podcasts")
        data class Podcast(
                @PrimaryKey val id: Int,
                @ColumnInfo(name = "title") val title: String,
                @ColumnInfo(name = "image_url") val imageUrl: String?,
                @ColumnInfo(name = "feed_url") val feedUrl:String,
                @ColumnInfo(name = "description") val description: String?,
                @ColumnInfo(name = "num_episodes") val numEpisodes: Int,
                @ColumnInfo(name = "subscribed") val subscribed:Boolean = false

        )

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertPodcast(vararg podcast: Podcast)

        @Update
        suspend fun updatePodcast(vararg podcast: Podcast)

        @Delete
        suspend fun deletePodcast(vararg podcast: Podcast)

        @Query("SELECT * FROM podcasts WHERE id = :id")
        fun getPodcast(id:Int):Podcast

        @Query("SELECT * FROM podcasts WHERE id = :id")
        fun loadPodcastById(id: Int): LiveData<Podcast>

        @Query("SELECT * FROM podcasts WHERE subscribed = 1")
        fun loadSubscriptions(): LiveData<List<Podcast>>

}


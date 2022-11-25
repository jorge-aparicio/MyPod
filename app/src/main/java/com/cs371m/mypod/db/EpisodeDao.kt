package com.cs371m.mypod.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EpisodeDao {
    @Entity(tableName = "episodes",
        foreignKeys = [
            ForeignKey(entity = PodcastDao.Podcast::class,
                parentColumns = ["id"],
                childColumns = ["podcast_id"],
                onDelete = ForeignKey.CASCADE
            )])
    data class Episode(
        @PrimaryKey val id:String,
        @ColumnInfo(name = "title")val title: String,
        @ColumnInfo(name = "audio_url")val audioUrl: String,
        @ColumnInfo(name = "image_url")val imageUrl: String?,
        @ColumnInfo(name = "pub_date")val pubDate: String?,
        @ColumnInfo(name = "duration")val duration: String?,
        @ColumnInfo(name = "podcast_id")val podcastId : String,
        @ColumnInfo(name = "episode_number") val episodeNumber: Int,
        @ColumnInfo(name = "started")val started: Boolean = false,
        @ColumnInfo(name = "progress")val progress: Int = 0,
        @ColumnInfo(name= "played") val played:Boolean = false
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEpisode(vararg episode: Episode)

    @Update
    suspend fun updateEpisode(vararg episode: Episode)

    @Delete
    suspend fun deleteEpisode(vararg episode: Episode)

    @Query("SELECT * FROM episodes WHERE id = :id")
    fun getEpisodeById(id: Int): LiveData<Episode>

    @Query("SELECT * FROM episodes WHERE podcast_id = :podcastId")
    fun loadEpisodesByPodcastId(podcastId: String): LiveData<List<Episode>>

    @Query("SELECT a.*\n" +
            "FROM episodes a\n" +
            "INNER JOIN (\n" +
            "    SELECT id, MAX(episode_number) episode_number\n" +
            "    FROM episodes\n" +
            "    WHERE started = 0" +
            "    GROUP BY id\n" +
            ") b ON a.id = b.id AND a.episode_number = b.episode_number")
    fun getLatestEpisodes(): LiveData<List<Episode>>

    @Query("SELECT * FROM episodes WHERE started = 1 AND played = 0 LIMIT 8")
    fun getUnfinished():LiveData<List<Episode>>
}
package com.cs371m.mypod.ui.search

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.databinding.ProfileRowBinding
import com.cs371m.mypod.db.EpisodeDao
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EpisodeRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<EpisodeDao.Episode, EpisodeRowAdapter.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<EpisodeDao.Episode>() {
        override fun areItemsTheSame(oldItem: EpisodeDao.Episode, newItem: EpisodeDao.Episode): Boolean {
            return oldItem.id == newItem.id;
        }

        override fun areContentsTheSame(oldItem: EpisodeDao.Episode, newItem: EpisodeDao.Episode): Boolean {
            return oldItem.title == newItem.title;
        }

    }

    // TODO: Use this for setting up what happens when you click on a tile
    inner class VH(val rowBinding: ProfileRowBinding) :  RecyclerView.ViewHolder(rowBinding.root){
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ProfileRowBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        val holder = VH(binding);
        return holder;
    }

    // TODO: Get image, title, etc
    override fun onBindViewHolder(holder: VH, position: Int) {
        val rowBinding = holder.rowBinding;

        val episode = getItem(position);
        rowBinding.episodeTitle.text = episode.title;
        if (episode.imageUrl != null){
            rowBinding.episodeImage.visibility = VISIBLE
            Glide.glideFetch(episode.imageUrl, episode.imageUrl, rowBinding.episodeImage)
        }
        else rowBinding.episodeImage.visibility = GONE
        rowBinding.episodeTime.text = episode.duration
        rowBinding.episodeDate.text = parseDate(episode.pubDate)

    }

    private fun parseDate(pubDate: String??):String{
        if(pubDate == null) return ""
        try{
            val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z")
            val datetime: LocalDateTime = LocalDateTime.parse(pubDate, formatter)
            return datetime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }
        catch(e: Exception){
            val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z")
            val datetime: LocalDateTime = LocalDateTime.parse(pubDate, formatter)
            return datetime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }
    }

}
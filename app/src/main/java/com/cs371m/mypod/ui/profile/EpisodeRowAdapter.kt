package com.cs371m.mypod.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.api.EpisodesQuery
import com.cs371m.mypod.api.type.DateTime
import com.cs371m.mypod.databinding.ProfileRowBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EpisodeRowAdapter(private val viewModel: MainViewModel)
        : ListAdapter<EpisodesQuery.Data1, EpisodeRowAdapter.VH>(PodcastDiff()) {

        class PodcastDiff : DiffUtil.ItemCallback<EpisodesQuery.Data1>() {
                override fun areItemsTheSame(oldItem: EpisodesQuery.Data1, newItem: EpisodesQuery.Data1): Boolean {
                        return oldItem.id == newItem.id;
                }

                override fun areContentsTheSame(oldItem: EpisodesQuery.Data1, newItem: EpisodesQuery.Data1): Boolean {
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
                if (episode.imageUrl != null)
                        Glide.glideFetch(episode.imageUrl.toString(), episode.imageUrl.toString(), rowBinding.episodeImage)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val datetime: LocalDateTime = LocalDateTime.parse(episode.airDate.toString(), formatter)

                rowBinding.episodeDate.text = datetime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))

                rowBinding.episodeTime.text = viewModel.convertTime(episode.length!!)

        }

}
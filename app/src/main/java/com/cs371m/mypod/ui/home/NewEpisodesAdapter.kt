package com.cs371m.mypod.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.db.EpisodeDao
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class NewEpisodesAdapter(private val viewModel: MainViewModel, private  val context: Context)
    : ListAdapter<EpisodeDao.Episode, NewEpisodesAdapter.VH>(EpisodeDiff()) {

    class EpisodeDiff : DiffUtil.ItemCallback<EpisodeDao.Episode>() {
        override fun areItemsTheSame(oldItem: EpisodeDao.Episode, newItem: EpisodeDao.Episode): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.title == newItem.title)
        }

        override fun areContentsTheSame(oldItem: EpisodeDao.Episode, newItem: EpisodeDao.Episode): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.title == newItem.title)
        }

    }

    // TODO: Use this for setting up what happens when you click on a tile
    inner class VH(val podTileBinding: PodTileBinding) :  RecyclerView.ViewHolder(podTileBinding.root) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = PodTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VH(binding)
        return holder
    }

    // TODO: Get image, title, etc
    override fun onBindViewHolder(holder: VH, position: Int) {
        val podTileBinding = holder.podTileBinding

        // Set the title
        val episode = getItem(holder.adapterPosition)
        podTileBinding.tileTitle.text = episode.title

        // Set the image
        Glide.glideFetch(episode.imageUrl!!, episode.imageUrl, podTileBinding.tileImage)

        // Play when clicked on
        podTileBinding.root.setOnClickListener {
            viewModel.setStarted(episode.id,true)
            viewModel.setCurrPlaying(episode)
        }
        podTileBinding.root.setOnLongClickListener {
            viewModel.showBottomSheetDialog(context,episode)
        }

    }

}
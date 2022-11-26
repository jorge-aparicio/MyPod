package com.cs371m.mypod.ui.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.R
import com.cs371m.mypod.db.PodcastDao
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class SubscriptionsAdaper(private val viewModel: MainViewModel)
    : ListAdapter<PodcastDao.Podcast, SubscriptionsAdaper.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<PodcastDao.Podcast>() {
        override fun areItemsTheSame(oldItem: PodcastDao.Podcast, newItem: PodcastDao.Podcast): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.title == newItem.title)
        }

        override fun areContentsTheSame(oldItem: PodcastDao.Podcast, newItem: PodcastDao.Podcast): Boolean {
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

    override fun onBindViewHolder(holder: VH, position: Int) {
        val podTileBinding = holder.podTileBinding

        // Set the title
        val podcast = getItem(holder.adapterPosition)
        podTileBinding.tileTitle.text = podcast.title

        podTileBinding.root.setOnClickListener {
            viewModel.updateProfile(podcast.id)
            viewModel.getNavController().navigate(R.id.navigation_profile)
        }
        // Set the image
        Glide.glideFetch(podcast.imageUrl!!, podcast.imageUrl, podTileBinding.tileImage)
    }

}
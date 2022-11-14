package com.cs371m.mypod.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.api.ITunesAPI
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class SubscriptionsAdaper(private val viewModel: MainViewModel)
    : ListAdapter<ITunesAPI.PodcastArtist, SubscriptionsAdaper.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<ITunesAPI.PodcastArtist>() {
        override fun areItemsTheSame(oldItem: ITunesAPI.PodcastArtist, newItem: ITunesAPI.PodcastArtist): Boolean {
            return (oldItem.artistId == newItem.artistId) && (oldItem.artistName == newItem.artistName);
        }

        override fun areContentsTheSame(oldItem: ITunesAPI.PodcastArtist, newItem: ITunesAPI.PodcastArtist): Boolean {
            return (oldItem.artistId == newItem.artistId) && (oldItem.artistName == newItem.artistName);
        }

    }

    // TODO: Use this for setting up what happens when you click on a tile
    inner class VH(val podTileBinding: PodTileBinding) :  RecyclerView.ViewHolder(podTileBinding.root) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = PodTileBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        val holder = VH(binding);
        return holder;
    }

    // TODO: Get image, title, etc
    override fun onBindViewHolder(holder: VH, position: Int) {
        val podTileBinding = holder.podTileBinding;

        // Set the title
        val podcast = getItem(holder.adapterPosition);
        podTileBinding.tileTitle.text = podcast.artistName;

        // Set the image
        Glide.glideFetch(podcast.imageUrl, podcast.imageUrl, podTileBinding.tileImage)
    }

}
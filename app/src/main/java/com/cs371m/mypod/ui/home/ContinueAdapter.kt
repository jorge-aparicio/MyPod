package com.cs371m.mypod.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.api.ITunesAPI
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class ContinueAdapter(private val viewModel: MainViewModel)
    : ListAdapter<ITunesAPI.Episode, ContinueAdapter.VH>(EpisodeDiff()) {

    class EpisodeDiff : DiffUtil.ItemCallback<ITunesAPI.Episode>() {
        override fun areItemsTheSame(oldItem: ITunesAPI.Episode, newItem: ITunesAPI.Episode): Boolean {
            return (oldItem.trackId == newItem.trackId) && (oldItem.trackName == newItem.trackName);
        }

        override fun areContentsTheSame(oldItem: ITunesAPI.Episode, newItem: ITunesAPI.Episode): Boolean {
            return (oldItem.trackId == newItem.trackId) && (oldItem.trackName == newItem.trackName);
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
        val episode = getItem(holder.adapterPosition);
        podTileBinding.tileTitle.text = episode.artistName;

        // Set the image
        Glide.glideFetch(episode.artworkUrl600, episode.artworkUrl600, podTileBinding.tileImage)
    }

}
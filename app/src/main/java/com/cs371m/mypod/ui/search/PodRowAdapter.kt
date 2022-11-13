package com.cs371m.mypod.ui.search

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.api.PodcastSearchQuery
import com.cs371m.mypod.api.ProfileQuery
import com.cs371m.mypod.databinding.PodRowBinding
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class PodRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<PodcastSearchQuery.Data1, PodRowAdapter.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<PodcastSearchQuery.Data1>() {
        override fun areItemsTheSame(oldItem: PodcastSearchQuery.Data1, newItem: PodcastSearchQuery.Data1): Boolean {
            return oldItem.applePodcastsId == newItem.applePodcastsId;
        }

        override fun areContentsTheSame(oldItem: PodcastSearchQuery.Data1, newItem: PodcastSearchQuery.Data1): Boolean {
            return oldItem.applePodcastsId == newItem.applePodcastsId;
        }

    }

    // TODO: Use this for setting up what happens when you click on a tile
    inner class VH(val podRowBinding: PodRowBinding) :  RecyclerView.ViewHolder(podRowBinding.root) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = PodRowBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        val holder = VH(binding);
        return holder;
    }

    // TODO: Get image, title, etc
    override fun onBindViewHolder(holder: VH, position: Int) {
        val podRowBinding = holder.podRowBinding;

        val podcast = getItem(position);
        podRowBinding.rowTitle.text = podcast.title;
        if (podcast.imageUrl != null)
            Glide.glideFetch(podcast.imageUrl.toString(), podcast.imageUrl.toString(), podRowBinding.rowImage)

    }

}
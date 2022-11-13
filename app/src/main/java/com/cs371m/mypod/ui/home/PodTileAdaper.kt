package com.cs371m.mypod.ui.home

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.api.ProfileQuery
import com.cs371m.mypod.databinding.PodTileBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class PodTileAdapter(private val viewModel: MainViewModel)
    : ListAdapter<ProfileQuery.Podcast, PodTileAdapter.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<ProfileQuery.Podcast>() {
        override fun areItemsTheSame(oldItem: ProfileQuery.Podcast, newItem: ProfileQuery.Podcast): Boolean {
            return oldItem.id == newItem.id;
        }

        override fun areContentsTheSame(oldItem: ProfileQuery.Podcast, newItem: ProfileQuery.Podcast): Boolean {
            return oldItem.id == newItem.id;
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

        val podcast = getItem(position);
        podTileBinding.tileTitle.text = podcast.title;
        if (podcast.imageUrl != null)
            Glide.glideFetch(podcast.imageUrl.toString(), podcast.imageUrl.toString(), podTileBinding.tileImage)
        Log.d("##########################", "${podcast.title}")

    }

}
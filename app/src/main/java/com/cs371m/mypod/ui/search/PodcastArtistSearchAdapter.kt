package com.cs371m.mypod.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cs371m.mypod.R
import com.cs371m.mypod.api.ITunesAPI
import com.cs371m.mypod.databinding.PodRowBinding
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel

class PodcastArtistSearchAdapter(private val viewModel: MainViewModel)
    : ListAdapter<ITunesAPI.Podcast, PodcastArtistSearchAdapter.VH>(PodcastDiff()) {

    class PodcastDiff : DiffUtil.ItemCallback<ITunesAPI.Podcast>() {
        override fun areItemsTheSame(oldItem: ITunesAPI.Podcast, newItem: ITunesAPI.Podcast): Boolean {
            return oldItem.collectionId == newItem.collectionId;
        }

        override fun areContentsTheSame(oldItem: ITunesAPI.Podcast, newItem: ITunesAPI.Podcast): Boolean {
            return oldItem.collectionId == newItem.collectionId;
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
        podRowBinding.rowTitle.text = podcast.collectionName;
        Glide.glideFetch(podcast.artworkUrl100, podcast.artworkUrl100, podRowBinding.rowImage)

        podRowBinding.root.setOnClickListener(){
            viewModel.updateProfile(podcast.collectionId.toInt())
            viewModel.getNavController().navigate(R.id.navigation_profile)


        }

    }

}
package com.cs371m.mypod.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs371m.mypod.databinding.FragmentPodProfileBinding
import com.cs371m.mypod.db.PodcastDao
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import com.cs371m.mypod.ui.search.EpisodeRowAdapter

//import com.cs371m.mypod.ui.search.EpisodeRowAdapter


class ProfileFragment : Fragment() {
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
        private var _binding: FragmentPodProfileBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        // API Stuff
        private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            _binding = FragmentPodProfileBinding.inflate(inflater, container, false)
            val root: View = binding.root

            return root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = EpisodeRowAdapter(viewModel)
        binding.episodeList.adapter = adapter
        binding.episodeList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        viewModel.observePodcastProfile().observe(viewLifecycleOwner){ it:PodcastDao.Podcast ->
            if(it.subscribed){
                binding.subscribeButton.text = "Unsubscribe"
            }else{
                binding.subscribeButton.text = "Subscribe"
            }

            adapter.setPodcastName(it.title)
            binding.podcastTitle.text=it.title
            binding.podcastDescription.text = it.description
            binding.episodeDate.text = it.numEpisodes.toString() + " episodes"

            if (it.imageUrl != null)
                Glide.glideFetch(it.imageUrl, it.imageUrl, binding.podcastImage)
        }
        binding.subscribeButton.setOnClickListener {
                if (binding.subscribeButton.text == "Subscribe") {
                    binding.subscribeButton.text = "Unsubscribe"

                } else {
                    binding.subscribeButton.text = "Subscribe"

                }
                viewModel.toggleSubscribed()
            }



        viewModel.observeProfileEpisodes().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }



    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

    }
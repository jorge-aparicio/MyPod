package com.cs371m.mypod.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs371m.mypod.databinding.FragmentSearchBinding
import com.cs371m.mypod.ui.MainViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // API Stuff
    private val viewModel: MainViewModel by activityViewModels();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search State (true = podcast, false = episode)
        val podcastSearch = MutableLiveData<Boolean>();

        // Set up initial state
        binding.podcastToggle.isChecked = true;
        binding.episodeToggle.isChecked = false;
        podcastSearch.postValue(true);
        var searchTerm: String = "";

        // Set up initial adapters
        val episodeSearchAdapter = EpisodeSearchAdapter(viewModel);
        val podcastArtistSearchAdapter = PodcastArtistSearchAdapter(viewModel);
        binding.searchList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);

        // Handle Toggles
        val podcastToggle = binding.podcastToggle;
        val episodeToggle = binding.episodeToggle;
        binding.podcastToggle.setOnClickListener {
            podcastToggle.isChecked = true;
            episodeToggle.isChecked = false;
            // Switch state
            podcastSearch.postValue(true);
        }
        binding.episodeToggle.setOnClickListener {
            podcastToggle.isChecked = false;
            episodeToggle.isChecked = true;
            // Switch state
            podcastSearch.postValue(false);
        }

        // Switch adapter on toggles
        podcastSearch.observe(viewLifecycleOwner) {
            if (it) {
                binding.searchList.adapter = podcastArtistSearchAdapter;
            }
            else {
                binding.searchList.adapter = episodeSearchAdapter;
            }
        }

        // Handle user input
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                // Search based on state
                if(query!=null && query!!.length > 2) {

                    // Podcast Search
                    if (binding.podcastToggle.isChecked) {
                        viewModel.searchPodcastArtists(query, 5);
                    }
                    // Episode Search
                    if (binding.episodeToggle.isChecked) {
                        viewModel.searchEpisodes(query, 20);
                    }

                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // Set state
                searchTerm = newText.toString();

                // Search based on state
                if(newText!=null && newText!!.length > 2) {

//                    // Podcast Search
//                    if (binding.podcastToggle.isChecked) {
//                        viewModel.searchPodcastArtists(newText, 20);
//                    }
//                    // Episode Search
//                    if (binding.episodeToggle.isChecked) {
//                        viewModel.searchEpisodes(newText, 20);
//                    }

                    return true
                }
                return false
            }

        })

        // Display results
        viewModel.observePodcastArtistSearchResults().observe(viewLifecycleOwner) {
            podcastArtistSearchAdapter.submitList(it);
            podcastArtistSearchAdapter.notifyDataSetChanged();
        }
        viewModel.observeEpisodeSearchResults().observe(viewLifecycleOwner) {
            episodeSearchAdapter.submitList(it);
            episodeSearchAdapter.notifyDataSetChanged();
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
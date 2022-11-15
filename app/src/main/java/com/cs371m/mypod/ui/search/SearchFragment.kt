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

        podcastSearch.postValue(true);
        var searchTerm: String = "";

        // Set up initial adapters
        val podcastArtistSearchAdapter = PodcastArtistSearchAdapter(viewModel);
        binding.searchList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);

        viewModel.getTop25();
        // Switch adapter on toggles
        podcastSearch.observe(viewLifecycleOwner) {

                binding.searchList.adapter = podcastArtistSearchAdapter;

        }

        // Handle user input
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                // Search based on state
                if(query!=null && query!!.length > 2) {
                        viewModel.searchPodcasts(query, 15);
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // Set state
                searchTerm = newText.toString();

                // Search based on state
                if(newText!=null && newText!!.length > 2) {


                        viewModel.searchPodcasts(newText, 20);


                    return true
                }
                if(newText!!.isEmpty()) {
                    viewModel.getTop25()
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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
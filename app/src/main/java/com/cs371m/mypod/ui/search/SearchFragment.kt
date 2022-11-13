package com.cs371m.mypod.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs371m.mypod.api.PodcastSearchQuery
import com.cs371m.mypod.databinding.FragmentSearchBinding
import com.cs371m.mypod.ui.MainViewModel
import com.cs371m.mypod.ui.home.PodTileAdapter

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
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapter
        val adapter = PodRowAdapter(viewModel);
        binding.searchSubsList.adapter = adapter;
        binding.searchSubsList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        viewModel.searchPodcasts("crime", 10)

        // Display podcasts
        viewModel.observeSearchResults().observe(viewLifecycleOwner) {
            adapter.submitList(it);
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
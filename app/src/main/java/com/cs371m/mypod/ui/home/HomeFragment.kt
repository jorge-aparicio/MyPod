package com.cs371m.mypod.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs371m.mypod.databinding.FragmentHomeBinding
import com.cs371m.mypod.ui.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.homeSubsText
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        // Set up adapter
        val adapter = PodTileAdapter(viewModel);
        binding.continueList.adapter = adapter;
        binding.continueList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        val podList = mutableListOf<String>();
        podList.add("998568017");
        podList.add("1322200189")
        podList.add("949195280")
        podList.add("1401698612")
        podList.add("1112004494")
        viewModel.setPodcastsList(podList);

        // Search for podcast data everytime list is updated
        viewModel.observePodcastsList().observe(viewLifecycleOwner) {
            viewModel.searchPodcastsList();
        }

        // Display podcasts
        viewModel.observePodcastsDataList().observe(viewLifecycleOwner) {
            adapter.submitList(it);
            Log.d("XXXXXXXXXXXXXXXXXX", "${binding.continueList.size}")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
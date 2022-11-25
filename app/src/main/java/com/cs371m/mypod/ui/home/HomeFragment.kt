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
import androidx.recyclerview.widget.GridLayoutManager
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.homeSubsText

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        // ########### TEST ###########
        val testEpisodes = mutableListOf<String>();
        testEpisodes.add("1405972616");
        testEpisodes.add("1495608381");
        testEpisodes.add("503300852");
        testEpisodes.add("1322200189");
        testEpisodes.add("956276360");
        testEpisodes.add("626605826");
        viewModel.setContinueList(testEpisodes);

        // ########### TEST ###########

        // TODO: Need to get subscriptions & continue listening lists from a database

        // Set up adapters

        val continueAdapter = ContinueAdapter(viewModel);
        binding.continueList.adapter = continueAdapter;
        binding.continueList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);

        // Subscription List Observers
        viewModel.observeSubscriptionList().observe(viewLifecycleOwner) {
        }


        // Continue Listening List Observers
        viewModel.observeContinueList().observe(viewLifecycleOwner) {
            viewModel.processContinueList();
        }
        viewModel.observeContinueListData().observe(viewLifecycleOwner) {
            Log.d("#################################################", "Continue Listening List Changed (Size: ${it.size})")
            continueAdapter.submitList(it);
            continueAdapter.notifyDataSetChanged();
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
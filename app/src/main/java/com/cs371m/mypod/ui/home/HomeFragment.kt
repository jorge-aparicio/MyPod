package com.cs371m.mypod.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        // Set up adapters
        val continueAdapter = ContinueAdapter(viewModel);
        binding.continueList.adapter = continueAdapter;
        binding.continueList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);

        val newEpisodesAdapter = NewEpisodesAdapter(viewModel)
        binding.newEpsList.adapter = newEpisodesAdapter
        binding.newEpsList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);

        // Subscription List Observers
        viewModel.observeNewEpsList().observe(viewLifecycleOwner) {
        newEpisodesAdapter.submitList(it)
            newEpisodesAdapter.notifyDataSetChanged()

        }


        viewModel.observeContinueList().observe(viewLifecycleOwner) {
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
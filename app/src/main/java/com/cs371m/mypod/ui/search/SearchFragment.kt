package com.cs371m.mypod.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        // Set up adapter
        val adapter = PodRowAdapter(viewModel);
        binding.searchSubsList.adapter = adapter;
        binding.searchSubsList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
// do something on text submit
                if(query!=null) {
                    viewModel.searchPodcasts(query!!, 10)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
// do something when text changes
                if(newText!=null && newText!!.length > 2) {
                    viewModel.searchPodcasts(newText!!, 10)
                    return true
                }
                return false
            }
        })


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
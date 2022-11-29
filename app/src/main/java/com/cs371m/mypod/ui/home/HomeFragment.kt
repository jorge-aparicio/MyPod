package com.cs371m.mypod.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs371m.mypod.R
import com.cs371m.mypod.databinding.FragmentHomeBinding
import com.cs371m.mypod.ui.MainViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var continueAdapter:ContinueAdapter
    private lateinit var newEpisodesAdapter: NewEpisodesAdapter

    // API Stuff
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adaptersegisterForContextMenu(
        continueAdapter = ContinueAdapter(viewModel,this.requireContext())
        binding.continueList.adapter = continueAdapter
        binding.continueList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        newEpisodesAdapter = NewEpisodesAdapter(viewModel, this.requireContext())
        binding.newEpsList.adapter = newEpisodesAdapter
        binding.newEpsList.layoutManager = GridLayoutManager(this.context, 4)
        registerForContextMenu(binding.continueList)
        registerForContextMenu(binding.newEpsList)
        // Subscription List Observers
        viewModel.observeNewEpsList().observe(viewLifecycleOwner) {
        newEpisodesAdapter.submitList(it)
            newEpisodesAdapter.notifyDataSetChanged()

        }


        viewModel.observeContinueList().observe(viewLifecycleOwner) {
            Log.d("#################################################", "Continue Listening List Changed (Size: ${it.size})")
            continueAdapter.submitList(it)
            continueAdapter.notifyDataSetChanged()
        }

    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.episode_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        println( info.targetView)
        println(info.id)
        println(info.position)
        when (item.itemId) {
            R.id.menu_play -> {
                println("Playing ")
            }
            R.id.menu_mark_played ->{
                println("Marking Played")
            }
            R.id.menu_download->{
                println("Downloading")
            }
            R.id.menu_share -> {
                println("Sharing")
            }
        }
        return super.onContextItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
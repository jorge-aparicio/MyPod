package com.cs371m.mypod.ui.subscriptions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cs371m.mypod.databinding.FragmentSubsBinding
import com.cs371m.mypod.ui.MainViewModel

class SubscriptionsFragment : Fragment() {

    private var _binding: FragmentSubsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val notificationsViewModel =
//            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentSubsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up adapter
        val subAdapter = SubscriptionsAdaper(viewModel)
        binding.subFragList.adapter = subAdapter
        binding.subFragList.layoutManager = GridLayoutManager(this.context, 4)

        // Subscription List Observers
        viewModel.observeSubscriptionList().observe(viewLifecycleOwner) {
            Log.d("#################################################", "Subscription List Changed (Size: ${it.size})")
            subAdapter.submitList(it)
            subAdapter.notifyDataSetChanged()
        }

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
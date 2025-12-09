package com.reborn.wasteless.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reborn.wasteless.databinding.FragmentHomeBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reborn.wasteless.ui.adapter.FoodLogAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        //Local variable root that points to binding.root
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe greeting from ViewModel - using viewLifecycleOwner prevents memory leaks
        // This automatically unsubscribes when the view is destroyed
        vm.greeting.observe(viewLifecycleOwner) { greetingText ->
            binding.textGreeting.text = greetingText
        }

        binding.buttonLogWaste.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToLogging())
        }

        /**
         * RecyclerView summary mapping
         */
        val recyclerDiary = binding.recyclerHomepageDiary

        //Pass the click listener (for safe args as well)
        val adapter = FoodLogAdapter(mode = "HOME") { summary ->
            // Use SafeArgs to pass the ID
            val action = HomeFragmentDirections.actionHomeToLogging(logId = summary.id)
            findNavController().navigate(action)
        }

        recyclerDiary.adapter = adapter
        recyclerDiary.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vm.summary.observe(viewLifecycleOwner) { summaries ->
            adapter.updateData(summaries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
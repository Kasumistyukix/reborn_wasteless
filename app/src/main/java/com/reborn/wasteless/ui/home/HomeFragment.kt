package com.reborn.wasteless.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.reborn.wasteless.databinding.FragmentHomeBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
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
        recyclerDiary.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vm.summary.observe(viewLifecycleOwner) { summaries ->
            val adapter = FoodLogAdapter(summaries, mode = "HOME")
            recyclerDiary.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
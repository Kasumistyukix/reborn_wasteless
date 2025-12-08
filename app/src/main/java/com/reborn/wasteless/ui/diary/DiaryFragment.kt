package com.reborn.wasteless.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reborn.wasteless.databinding.FragmentDiaryBinding
import com.reborn.wasteless.ui.adapter.FoodLogAdapter
import kotlin.getValue
import com.reborn.wasteless.utils.applyTopWindowInsets


class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private val vm: DiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Apply padding to toolbar
        binding.diaryToolbar.applyTopWindowInsets()

        /**
         * RecyclerView summary mapping
         */
        val recyclerDiary = binding.recyclerDiary
        recyclerDiary.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        vm.summary.observe(viewLifecycleOwner) { summaries ->
            val adapter = FoodLogAdapter(summaries, mode = "DIARY")
            recyclerDiary.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
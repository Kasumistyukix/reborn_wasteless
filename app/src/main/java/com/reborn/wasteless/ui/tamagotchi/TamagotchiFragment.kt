package com.reborn.wasteless.ui.tamagotchi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.reborn.wasteless.databinding.FragmentTamagotchiBinding
import kotlin.getValue

class TamagotchiFragment : Fragment() {

    private var _binding: FragmentTamagotchiBinding? = null
    private val binding get() = _binding!!
    private val vm: TamagotchiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTamagotchiBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
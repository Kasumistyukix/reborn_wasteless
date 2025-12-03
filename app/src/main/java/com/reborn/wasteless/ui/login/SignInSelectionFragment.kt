package com.reborn.wasteless.ui.login

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reborn.wasteless.R
import com.reborn.wasteless.databinding.FragmentSignInSelectionBinding

class SignInSelectionFragment : Fragment() {

    companion object {
        fun newInstance() = SignInSelectionFragment()
    }

    private var _binding: FragmentSignInSelectionBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SignInSelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sign_in_selection, container, false)
    }
}
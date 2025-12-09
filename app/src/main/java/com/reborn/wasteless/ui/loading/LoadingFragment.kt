package com.reborn.wasteless.ui.loading

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.reborn.wasteless.R
import com.reborn.wasteless.repo.AuthRepository

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val authRepository = AuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check if the user is signed in
        val isSignedIn = authRepository.isUserSignedIn()

        // 2. Direct traffic
        if (isSignedIn) {
            findNavController().navigate(LoadingFragmentDirections.actionLoadingToHome())
        } else {
            findNavController().navigate(LoadingFragmentDirections.actionLoadingToSignIn())
        }
    }
}
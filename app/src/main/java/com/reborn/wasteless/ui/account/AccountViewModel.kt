package com.reborn.wasteless.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.repo.AuthRepository

class AccountViewModel (
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    /**
     * Add a logged out state to allow the ui to observe state changes
     */
    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> get() = _loggedOut

    fun logOut() {
        authRepository.signOut()
        _loggedOut.value = true
    }
}
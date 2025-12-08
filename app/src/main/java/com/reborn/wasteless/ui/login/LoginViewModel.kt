package com.reborn.wasteless.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.data.model.AuthState
import com.reborn.wasteless.repo.AuthRepository

/**
 * ViewModel for LoginFragment.
 *
 * Responsibilities:
 * - Validates login credentials
 * - Handles login business logic
 * - Exposes login state via LiveData
 *
 * Memory leak prevention:
 * - Uses LiveData (lifecycle-aware)
 * - No direct Android dependencies
 * - Repository handles Firebase operations
 *
 * Usage in Fragment:
 * ```kotlin
 * viewModel.login(email, password)
 * viewModel.loginState.observe(viewLifecycleOwner) { state ->
 *     when (state) {
 *         is AuthState.Success -> navigateToHome()
 *         is AuthState.Error -> showError(state.message)
 *         is AuthState.Loading -> showProgressBar()
 *         AuthState.Idle -> // Do nothing
 *     }
 * }
 * ```
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    /**
     * LiveData representing the current login state.
     * Fragment should observe this to react to login results.
     */
    private val _loginState = MutableLiveData<AuthState>(AuthState.Idle)
    val loginState: LiveData<AuthState> = _loginState

    /**
     * Attempts to sign in a user with email and password.
     *
     * Flow:
     * 1. Sets state to Loading
     * 2. Validates input
     * 3. Calls repository to sign in
     * 4. Updates state to Success or Error
     *
     * @param email User's email address
     * @param password User's password
     */
    fun login(email: String, password: String) {
        // Validate input
        if (email.isBlank()) {
            _loginState.value = AuthState.Error("Email cannot be empty")
            return
        }

        if (password.isBlank()) {
            _loginState.value = AuthState.Error("Password cannot be empty")
            return
        }

        // Set loading state
        _loginState.value = AuthState.Loading

        // Call repository to sign in
        authRepository.signIn(email.trim(), password)
            .addOnSuccessListener { authResult ->
                // Login successful
                val userEmail = authResult.user?.email ?: email.trim()
                _loginState.value = AuthState.Success(userEmail)
            }
            .addOnFailureListener { exception ->
                // Login failed - extract user-friendly error message
                // Basically same thing as signup, Firebase sends error messages by default when a function fails
                // So rn, we check if the error messages contain any of the "keywords" i.e. email or password
                val errorMessage = when {
                    exception.message?.contains("password") == true ->
                        "Invalid email or password"
                    exception.message?.contains("network") == true ->
                        "Network error. Please check your connection"
                    exception.message?.contains("user") == true ->
                        "No account found with this email"
                    else ->
                        exception.message ?: "Login failed. Please try again"
                }
                _loginState.value = AuthState.Error(errorMessage)
            }
    }

    /**
     * Resets the login state to Idle.
     * Call this when navigating away or clearing the form.
     */
    fun resetState() {
        _loginState.value = AuthState.Idle
    }
}



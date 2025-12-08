package com.reborn.wasteless.ui.signup

import android.util.Log
import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.data.model.AuthState
import com.reborn.wasteless.repo.AuthRepository
import com.reborn.wasteless.repo.UserRepository

/**
 * ViewModel for SignUpFragment.
 *
 * Responsibilities:
 * - Validates registration input (email, password)
 * - Handles signup business logic
 * - Creates user document in Firestore after successful signup
 * - Exposes registration state via LiveData
 *
 * Memory leak prevention:
 * - Uses LiveData (lifecycle-aware)
 * - No direct Android dependencies
 * - Repository handles Firebase operations
 *
 * Usage in Fragment:
 * ```kotlin
 * viewModel.register(email, password)
 * viewModel.registerState.observe(viewLifecycleOwner) { state ->
 *     when (state) {
 *         is AuthState.Success -> navigateToLogin()
 *         is AuthState.Error -> showError(state.message)
 *         is AuthState.Loading -> showProgressBar()
 *         AuthState.Idle -> // Do nothing
 *     }
 * }
 * ```
 */
class SignUpViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    /**
     * LiveData representing the current registration state.
     * Fragment should observe this to react to registration results.
     */
    private val _registerState = MutableLiveData<AuthState>(AuthState.Idle)
    val registerState: LiveData<AuthState> = _registerState

    /**
     * Attempts to create a new user account.
     *
     * Flow:
     * 1. Sets state to Loading
     * 2. Validates input (username, email format, password length)
     * 3. Calls repository to create account
     * 4. Creates user document in Firestore with username
     * 5. Updates state to Success or Error
     *
     * @param username User's display name/username
     * @param email User's email address
     * @param password User's password (must be at least 6 characters)
     */
    fun register(username: String, email: String, password: String) {
        //Trims whitespace from the start and end, if any
        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isBlank()) {
            _registerState.value = AuthState.Error("Username cannot be empty")
            return
        }

        if (trimmedUsername.length < 3) {
            _registerState.value = AuthState.Error("Username must be at least 3 characters")
            return
        }

        if (trimmedEmail.isBlank()) {
            _registerState.value = AuthState.Error("Email cannot be empty")
            return
        }

        if (!isValidEmail(trimmedEmail)) {
            _registerState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        if (trimmedPassword.isBlank()) {
            _registerState.value = AuthState.Error("Password cannot be empty")
            return
        }

        if (trimmedPassword.length < 8 || trimmedPassword.length > 32) {
            _registerState.value = AuthState.Error("Password must be between 8~32 characters")
            return
        }

        if (!isValidPassword(trimmedPassword)) {
            _registerState.value =
                AuthState.Error("Password must contain at least 1 uppercase, lowercase, special character & number")
        }

        // Set loading state
        _registerState.value = AuthState.Loading

        //First we gotta check if the username is unique
        userRepository.isUsernameTaken(trimmedUsername)
            .addOnSuccessListener { exists ->
                if (exists) {
                    _registerState.value = AuthState.Error("Username is already taken")
                    return@addOnSuccessListener // @addonSuccessListener is a labeled return to exit only the lambda/if function
                }

                //If the username is available, proceed to registering the user on Firebase
                performSignUp(trimmedUsername, trimmedEmail, trimmedPassword)
            }
            .addOnFailureListener {
                _registerState.value = AuthState.Error("Failed to authenticate username availability")
            }
        }

    /**
     * Honestly- I had to search this up :skull: but this is
     * a pattern to check if it's a valid email
     * the [A-Za-z0-9+_.-]+ basically describes the list of allowed characters before @
     * "A-Za-z" is any uppercase/lowercase letter (A-Z & a-z)
     * "0-9" is digits
     * "+_.-" is just these 4 symbols being allowed as well
     * And the + outside means "one or more" of these characters
     *
     * @param email Email address to validate
     * @return true if email format is valid, false otherwise
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }

    /**
     * //    Regexp                Description
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
     * (?=\\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, but length must be at least 8 characters though
     * $                 # end-of-string
     * ^ some stack overflow ans i saw lol
     *
     * @param password Password to validate
     * @return true if valid password format, else return false
     */
    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    /**
     * Registers the user on Firebase Auth, and also store a record of the user based UserEntity params
     *
     * @param username Username
     * @param email Email address
     * @param password Password
     * @return AuthState message based on success/failure
     */
    private fun performSignUp(username: String, email: String, password: String) {
        authRepository.signUp(email,password)
            .addOnSuccessListener { authResult ->
                // Account created successfully
                val userEmail = authResult.user?.email ?: email
                //Fetch uid from firebase
                val uid = authResult.user?.uid

                if (uid != null) {
                    //Set Firebase Auth displayName (quick access, no DB read needed)
                    authRepository.updateDisplayName(username)
                        .addOnSuccessListener {
                            //Create user document in Firestore with username
                            val userEntity = com.reborn.wasteless.data.entity.UserEntity(
                                uid = uid,
                                email = userEmail,
                                username = username, // Stored in Firestore
                                createdAt = System.currentTimeMillis()
                            )

                            //Now, set the username
                            userRepository.createOrUpdateUser(userEntity)
                                .addOnSuccessListener {
                                    _registerState.value = AuthState.Success(userEmail)
                                }
                                .addOnFailureListener { exception ->
                                    // Account created and displayName set, but Firestore update failed
                                    // displayName is still available via Firebase Auth
                                    _registerState.value = AuthState.Success(userEmail)
                                }
                        }
                        .addOnFailureListener { exception ->
                            // Account created but displayName update failed
                            // Still create Firestore document
                            val userEntity = com.reborn.wasteless.data.entity.UserEntity(
                                uid = uid,
                                email = userEmail,
                                username = username,
                                createdAt = System.currentTimeMillis()
                            )

                            //In the event Display Name upd fails straight away, store firebase document
                            //bcos it won't create a docs if it fails
                            userRepository.createOrUpdateUser(userEntity)
                                .addOnSuccessListener {
                                    _registerState.value = AuthState.Success(userEmail)
                                }
                                .addOnFailureListener {
                                    // Both failed, but account exists - still success
                                    _registerState.value = AuthState.Success(userEmail)
                                }
                        }
                } else {
                        _registerState.value = AuthState.Success(userEmail)
                }
            }
            .addOnFailureListener { exception ->
                // Registration failed, one of these reasons
                // Basically apparently, Firebase sends error messages by default when you fail a "createUserwithEmailandPassword()"
                // So rn, we check if the error messages contain any of the "keywords" i.e. email or password
                val errorMessage = when {
                    exception.message?.contains("email") == true &&
                            exception.message?.contains("already") == true ->
                        "An account with this email already exists"

                    exception.message?.contains("password") == true ->
                        "Password is too weak. Use 8~32 characters"

                    exception.message?.contains("network") == true ->
                        "Network error. Please check your connection"

                    exception.message?.contains("invalid") == true ->
                        "Invalid email format. Please use a valid email."

                    else ->
                        exception.message ?: "Registration failed. Please try again"
                }
                _registerState.value = AuthState.Error(errorMessage)
            }
    }

    /**
     * Resets the registration state to Idle.
     * Call this when navigating away or clearing the form.
     */
    fun resetState() {
        _registerState.value = AuthState.Idle
    }
}


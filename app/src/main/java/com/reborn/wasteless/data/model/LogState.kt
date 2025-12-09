package com.reborn.wasteless.data.model

sealed class LogState {
    /**
     * Logging operation is in progress.
     */
    object Loading : LogState()

    /**
     * Logging succeeded.
     * @param email The authenticated user's email
     */
    data class Success(val email: String) : LogState()

    /**
     * Logging failed.
     * @param message Error message to display to user, accept either string or int/id
     */
    data class Error(val message: String? = null, val messageId: Int? = null) : LogState()

    /**
     * Initial state - no operation has been performed yet.
     */
    object Idle : LogState()
}
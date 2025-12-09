package com.reborn.wasteless.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.R
import com.reborn.wasteless.repo.LogRepository

class DiaryViewModel(
    private val logRepository: LogRepository = LogRepository()
) : ViewModel() {

    //Get all summaries from logRepo
    val summary = logRepository.getAllSummaries()

    // LiveData to hold the message (Int for string resource IDs, or String for direct text)
    private val _deleteMessage = MutableLiveData<Int>()
    val deleteMessage: LiveData<Int> get() = _deleteMessage

    fun deleteLog(logId: String) {
        logRepository.deleteLog(logId)
            .addOnSuccessListener {
                //Delete success
                _deleteMessage.value = R.string.success_delete // Create this string in strings.xml
            }
            .addOnFailureListener { e ->
                //Failed to delete, pass error
                _deleteMessage.value = R.string.error_delete   // Create this string in strings.xml
            }
    }
}
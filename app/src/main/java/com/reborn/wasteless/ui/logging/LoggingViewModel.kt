package com.reborn.wasteless.ui.logging

import androidx.lifecycle.ViewModel
import com.reborn.wasteless.repo.LogRepository

class LoggingViewModel(
    private val logRepository: LogRepository = LogRepository()
) : ViewModel() {

}
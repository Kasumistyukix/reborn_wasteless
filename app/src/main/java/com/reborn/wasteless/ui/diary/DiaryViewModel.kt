package com.reborn.wasteless.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.reborn.wasteless.repo.LogRepository
import com.reborn.wasteless.data.model.FoodLogSummary
import com.reborn.wasteless.data.mappers.toSummary

class DiaryViewModel(
    private val logRepository: LogRepository = LogRepository()
) : ViewModel() {

    //Get all summaries from logRepo
    val summary = logRepository.getAllSummaries()
}
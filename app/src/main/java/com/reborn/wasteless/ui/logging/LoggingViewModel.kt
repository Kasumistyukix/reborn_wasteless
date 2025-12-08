package com.reborn.wasteless.ui.logging

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.data.CalcType
import com.reborn.wasteless.data.WasteType
import com.reborn.wasteless.repo.LogRepository

class LoggingViewModel(
    private val logRepository: LogRepository = LogRepository()
) : ViewModel() {

    // UI state
    val dateTime = MutableLiveData<Long>(System.currentTimeMillis())
    val title = MutableLiveData<String>("")
    val wasteType = MutableLiveData<WasteType>(WasteType.AVOIDABLE)
    val calcType = MutableLiveData<CalcType>(CalcType.PORTION)
    val remarks = MutableLiveData<String>("")
    val imageUri = MutableLiveData<Uri?>(null)
}
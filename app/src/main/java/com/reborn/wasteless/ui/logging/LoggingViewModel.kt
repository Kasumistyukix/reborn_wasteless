package com.reborn.wasteless.ui.logging

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reborn.wasteless.data.CalcType
import com.reborn.wasteless.data.WasteType
import com.reborn.wasteless.repo.LogRepository
import com.reborn.wasteless.data.entity.LoggedWasteItem
import com.reborn.wasteless.data.entity.FoodLogEntity
import com.reborn.wasteless.data.WasteItem
import com.reborn.wasteless.data.WasteItemsRepository
import com.reborn.wasteless.data.model.WasteItemSelection

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

    //Variables to track existing state for editing logs
    private var existingLogId: String? = null
    private var existingImageUrl: String? = null
    val existingImageToDisplay = MutableLiveData<String?>() // For loading img using Glide

    // Item selections kept per waste type so switching tabs does not lose input
    private val _selectionsByType = MutableLiveData<Map<WasteType, List<WasteItemSelection>>>(buildInitialSelections())
    private val _selections = MediatorLiveData<List<WasteItemSelection>>().apply {
        fun updateCurrentList() {
            val currentType = wasteType.value ?: WasteType.AVOIDABLE
            value = _selectionsByType.value.orEmpty()[currentType].orEmpty()
        }
        addSource(wasteType) { updateCurrentList() }
        addSource(_selectionsByType) { updateCurrentList() }
    }
    val selections: LiveData<List<WasteItemSelection>> = _selections

    fun updateQuantity(item: WasteItem, qty: Double) {
        val currentType = wasteType.value ?: WasteType.AVOIDABLE
        val currentMap = _selectionsByType.value.orEmpty()
        val updatedList = currentMap[currentType].orEmpty()
            .map { sel -> if (sel.item == item) sel.copy(quantity = qty) else sel }
        _selectionsByType.value = currentMap + (currentType to updatedList)
    }

    // Compute total weight live across all waste types
    val totalWeight: LiveData<Double> = MediatorLiveData<Double>().apply {
        fun recalc() {
            val allSelections = _selectionsByType.value.orEmpty().values.flatten()
            val ct = calcType.value ?: CalcType.PORTION
            value = allSelections.sumOf { sel ->
                if (ct == CalcType.GRAMS) sel.quantity
                else sel.quantity * sel.item.portionWeight
            }
        }
        addSource(_selectionsByType) { recalc() }
        addSource(calcType) { recalc() }
    }

    // Save‐status back to the Fragment
    private val _saveStatus = MutableLiveData<Result<Void>>()
    val saveStatus: LiveData<Result<Void>> = _saveStatus

    /**
     * Load an existing log (using recyclerview to get the id and then fetch data from firebase using that logId)
     * Also distributes the quantities (e.g. "Cookie: 2") into the correct lists (Avoidable/Unavoidable)
     * so they are ready when tabs are switched.
     */
    fun loadLog(logId: String) {
        logRepository.getLog(logId).addOnSuccessListener { entity ->
            if (entity != null) {
                existingLogId = entity.id
                existingImageUrl = entity.imageUrl

                dateTime.value = entity.date
                title.value = entity.title
                wasteType.value = entity.wasteType
                calcType.value = entity.calcType
                remarks.value = entity.remarks ?: ""
                existingImageToDisplay.value = entity.imageUrl

                // Reconstruct selections
                val initialMap = buildInitialSelections().toMutableMap()

                // We need to iterate over the saved items and update the initialMap
                entity.items.forEach { loggedItem ->
                    val type = loggedItem.wasteType
                    val list = initialMap[type]?.toMutableList() ?: return@forEach

                    // Find index of the item with the same name
                    val index = list.indexOfFirst { it.item.name == loggedItem.wasteItemId }
                    if (index != -1) {
                        list[index] = list[index].copy(quantity = loggedItem.quantity)
                        initialMap[type] = list
                    }
                }
                _selectionsByType.value = initialMap
            }
        }
    }

    /**
     * Upload image, then write one Firestore doc per item with qty>0
     */
    fun saveAll(context: Context) {
        val dt = dateTime.value ?: return
        val t = title.value.orEmpty()
        val type = wasteType.value ?: WasteType.AVOIDABLE
        val rem = remarks.value
        val uri = imageUri.value

        val itemsLog = _selectionsByType.value.orEmpty()
            .flatMap { (wasteTypeKey, list) ->
            list.filter { it.quantity > 0 }
            .map { sel ->
                val wt = if (calcType.value == CalcType.GRAMS)
                    sel.quantity
                else
                    sel.quantity * sel.item.portionWeight

                LoggedWasteItem(
                    wasteItemId = sel.item.name,
                    quantity = sel.quantity,
                    weight = wt,
                    wasteType = wasteTypeKey
                )
            }
                }

        val totalWt = itemsLog.sumOf { it.weight }
        val typesUsed = itemsLog.map { it.wasteType }.distinct()

        /**
         * Commit all changes made as a document under FoodLogEntity as a session
         * pushes image to ImageStore, and other data to Firestore under users > log > document collection
         */
        fun commit(imageUrl: String?) {
            val session = FoodLogEntity(
                //How this works is that it first checks if there was already a log id affiliated, and uses it if there was
                //If there wasn't just use the dt param as a new one
                id = existingLogId ?: dt.toString(),
                date = dt,
                title = t,
                wasteType = typesUsed.singleOrNull() ?: type,
                wasteTypes = typesUsed,
                calcType = calcType.value ?: CalcType.PORTION,
                totalWeight = totalWt,
                remarks = rem,
                imageUrl = imageUrl,
                items = itemsLog
            )
            logRepository.saveLog(session)
                .addOnSuccessListener { _saveStatus.value = Result.success(it) }
                .addOnFailureListener { _saveStatus.value = Result.failure(it) }
        }

        if (uri != null) {
            //If there was a new image selected, change/upload it
            logRepository.uploadImage(uri)
                .addOnSuccessListener { url -> commit(url) }
                .addOnFailureListener { e -> _saveStatus.value = Result.failure(e) }
        } else {
            //In the case that there was already an existing url, use that
            commit(existingImageUrl)
        }
    }

    private fun buildInitialSelections(): Map<WasteType, List<WasteItemSelection>> =
        WasteType.entries.associateWith { type ->
            WasteItemsRepository.getItemsForWasteType(type)
                .map { WasteItemSelection(it, quantity = 0.0) }
        }
}
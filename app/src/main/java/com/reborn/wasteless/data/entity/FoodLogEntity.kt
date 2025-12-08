package com.reborn.wasteless.data.entity

import com.reborn.wasteless.data.CalcType
import com.reborn.wasteless.data.WasteType

// Data/entity layer (Room entity, Firestore document, etc.)

/**
 * Entity for the food waste logs, it should be noted to myself that:
 * it is IMPORTANT very IMPORTANT to initialise these values first with something, or else firebase cannot deserialise it
 * without a no arg constructor
 *
 * Uhh searched it up, no arg constructor: just a way to create an object WITHOUT having to initialise/do something extra
 * So you can basically create it without doing anything, you just create it first
 */
data class FoodLogEntity(
    val id: String = "",
    val date: Long = 0L,
    val title: String = "",
    val wasteType: WasteType = WasteType.UNAVOIDABLE,
    val wasteTypes: List<WasteType> = emptyList(),
    val calcType: CalcType = CalcType.GRAMS,
    val totalWeight: Double = 0.0,     // calculated
    val remarks: String? = null,
    val imageUrl: String? = null,
    val items: List<LoggedWasteItem> = emptyList()   // e.g. "Fruit Peels, Weight, Qty. Refer to data class LoggedWasteItem"
)

data class LoggedWasteItem(
    val wasteItemId: String = "",
    val quantity: Double = 0.0,
    val weight: Double = 0.0,
    val wasteType: WasteType = WasteType.UNAVOIDABLE
)
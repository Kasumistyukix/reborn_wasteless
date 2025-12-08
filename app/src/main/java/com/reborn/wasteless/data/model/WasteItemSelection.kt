package com.reborn.wasteless.data.model

import com.reborn.wasteless.data.WasteItem

//Data class for waste type item recyclerview
//Represents a WasteItem with a user-selected quantity
data class WasteItemSelection(
    val item: WasteItem,
    var quantity: Double = 0.0
)
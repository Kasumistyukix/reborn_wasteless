package com.reborn.wasteless.data.model

// Decide what values to show in recyclerview, this is basically a host to fetch data
// Then later we can use a mapper to map the data
data class FoodLogSummary(
    val id: String,
    val title: String,
    val date: String,
    val totalWeight: String,
    val wasteType: String,
    val imageUrl: String?
)